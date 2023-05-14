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
import java8.util.concurrent.CompletableFuture;

import java.nio.ByteBuffer;

public class BasicFont extends AbstractGameResource implements Font {

    private final CompletableFuture<ByteBuffer> future;
    private final GLES gles;
    private volatile boolean done = false;
    private volatile ByteBuffer data;

    BasicFont(GLES gles, GameLauncher launcher, ResourceStream stream) {
        this.gles = gles;
        this.future = launcher.threads().workStealing.submit(() -> {
            byte[] b = stream.readAllBytes();
            stream.cleanup();
            this.data = gles.memoryManagement().alloc(b.length);
            this.data.put(b).flip();
            this.done = true;
            return data;
        });
    }

    @Override public ByteBuffer data() throws GameException {
        if (!this.done) {
            Threads.await(this.future);
        }
        return this.data;
    }

    @Override public CompletableFuture<ByteBuffer> dataFuture() {
        return future;
    }

    @Override public CompletableFuture<Void> cleanup0() throws GameException {
        return future.thenRun(() -> gles.memoryManagement().free(data));
    }
}
