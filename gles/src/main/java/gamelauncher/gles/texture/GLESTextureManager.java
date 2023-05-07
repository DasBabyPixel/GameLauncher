/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.texture;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Frame;
import gamelauncher.engine.render.texture.TextureManager;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.ExecutorThreadService;
import gamelauncher.gles.GLES;
import gamelauncher.gles.states.ContextLocal;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GLESTextureManager extends AbstractGameResource implements TextureManager {

    public final ExecutorThreadService service;
    public final GLES gles;
    public final ContextLocal<CLTextureUtility> clTextureUtility;
    private final Lock lock = new ReentrantLock(true);
    private Frame frame;
    private final GameLauncher launcher;

    public GLESTextureManager(GameLauncher launcher, GLES gles) {
        this.launcher = launcher;
        this.gles = gles;
        this.clTextureUtility = CLTextureUtility.local(gles);
        this.service = gles.launcher().threads().workStealing;
        this.frame = null;
    }

    public GameLauncher launcher() {
        return launcher;
    }

    @Override public GLESTexture createTexture() throws GameException {
        try {
            lock.lock();
            if (this.frame == null) {
                this.frame = gles.launcher().frame().newFrame();
            }
            return this.createTexture(this.frame.renderThread());
        } finally {
            lock.unlock();
        }
    }

    public GLESTexture createTexture(ExecutorThread owner) {
        return new GLESTexture(gles, owner, this.service);
    }

    @Override public void cleanup0() throws GameException {
        if (frame != null) frame.cleanup();
    }
}
