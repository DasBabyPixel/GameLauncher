/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.font.bitmap;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.font.Font;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.gles.GLES;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BasicFont extends AbstractGameResource implements Font {

    final AtomicInteger refcount = new AtomicInteger(0);
    final Lock lock = new ReentrantLock(true);
    private final CompletableFuture<Void> future;
    private final Path path;
    private final BasicFontFactory factory;
    private volatile boolean done = false;
    private volatile ByteBuffer data;
    private final GLES gles;

    BasicFont(GLES gles, BasicFontFactory factory, GameLauncher launcher, ResourceStream stream) {
        this.gles = gles;
        this.path = stream.getPath();
        this.factory = factory;
        this.future = launcher.threads().cached.submit(() -> {
            byte[] b = stream.readAllBytes();
            stream.cleanup();
            this.data = gles.memoryManagement().alloc(b.length);
            this.data.put(b).flip();
            this.done = true;
        });
    }

    @Override
    public ByteBuffer data() throws GameException {
        if (!this.done) {
            Threads.waitFor(this.future);
        }
        return this.data;
    }

    @Override
    public boolean cleanedUp() {
        return this.refcount.get() == 0;
    }

    @Override
    public void cleanup0() throws GameException {
        try {
            this.lock.lock();
            if (this.refcount.decrementAndGet() <= 0) {
                if (this.path != null) {
                    this.factory.fonts.remove(this.path);
                }
                if (!this.done) {
                    Threads.waitFor(this.future);
                }
                gles.memoryManagement().free(this.data);
            }
        } finally {
            this.lock.unlock();
        }
    }

}
