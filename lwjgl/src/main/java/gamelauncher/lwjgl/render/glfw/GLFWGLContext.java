/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl.render.glfw;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.logging.LogColor;
import gamelauncher.engine.util.logging.LogLevel;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.gles.gl.*;
import gamelauncher.gles.states.StateRegistry;
import gamelauncher.gles.util.GLDebugUtil;
import java8.util.concurrent.CompletableFuture;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GLFWGLContext extends AbstractGameResource implements GLContext {

    private static final Logger logger = Logger.logger();
    private static final AtomicInteger newId = new AtomicInteger(0);
    private static final LogLevel level = new LogLevel("GL", 10, new LogColor(0, 255, 255));
    final Collection<GLFWGLContext> sharedContexts;
    private final int id;
    volatile long glfwId;
    GLFWGLContext parent;
    ExecutorThread owner = null;
    GLFWFrame frame;
    private GameResource errorCallback = null;
    private boolean owned = true;
    private GLES32 gl;

    GLFWGLContext(Collection<GLFWGLContext> sharedContexts) {
        super();
        this.sharedContexts = sharedContexts;
        this.sharedContexts.add(this);
        id = newId.incrementAndGet();
    }

    @Override public synchronized void destroyCurrent() {
        if (this.owned) {
            StateRegistry.currentContext(null);
            GLFW.glfwMakeContextCurrent(0L);
            GLUtil.setNullCapabilities();
            this.owned = false;
            this.owner = null;
            if (errorCallback != null) {
                try {
                    errorCallback.cleanup();
                } catch (GameException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override public GLFWFrame frame() {
        return frame;
    }

    @Override public GLFWGLContext createSharedContext() throws GameException {
        synchronized (sharedContexts) {
            GLFWGLContext ctx = new GLFWGLContext(sharedContexts);
            ctx.parent = this;
            ctx.owned = false;
            frame.contexts.add(ctx);
            List<CompletableFuture<Void>> futs = new ArrayList<>(0);
            CompletableFuture<Void> selfFut = null;
            CompletableFuture<Void> mainFut = new CompletableFuture<>();
            for (GLFWGLContext c : ctx.sharedContexts) {
                if (c == ctx) continue;
                if (!c.owned) continue;
                CompletableFuture<Void> fut = new CompletableFuture<>();
                if (Thread.currentThread() != c.owner) {
                    c.owner.submit(() -> {
                        StateRegistry.currentContext(null);
                        GLFW.glfwMakeContextCurrent(0L);
                        fut.complete(null);
                        Threads.await(mainFut);
                        StateRegistry.currentContext(this);
                        GLFW.glfwMakeContextCurrent(c.glfwId);
                        GLUtil.createCapabilities();
                    });
                } else {
                    StateRegistry.currentContext(null);
                    GLFW.glfwMakeContextCurrent(0L);
                    fut.complete(null);
                    selfFut = fut;
                }
                futs.add(fut);
            }
            GLFWFrame f2 = new GLFWFrame(frame.launcher, ctx);
            ctx.frame = f2;
            Threads.whenAllComplete(futs, () -> frame.launcher().getGLFWThread().submit(() -> {
                ctx.create(this);
                f2.renderThread.start();
                mainFut.complete(null);
            }));
            if (selfFut != null) {
                Threads.await(mainFut);
                StateRegistry.currentContext(this);
                GLFW.glfwMakeContextCurrent(glfwId);
                GLUtil.createCapabilities();
            }
            return ctx;
        }
    }

    @Override public GLES20 gl20() {
        return gl;
    }

    @Override public GLES30 gl30() {
        return gl;
    }

    @Override public GLES31 gl31() {
        return gl;
    }

    @Override public GLES32 gl32() {
        return gl;
    }

    @Override public synchronized void makeCurrent() {
        makeCurrent(false);
    }

    @Override public String toString() {
        return String.format("GLFWGLContext{id=%s}", id);
    }

    @Override protected CompletableFuture<Void> cleanup0() throws GameException {
        synchronized (sharedContexts) {
            sharedContexts.remove(this);
            if (parent != null) {
                parent.frame.contexts.remove(this);
            }
            CompletableFuture<Void> f1 = null;
            if (this.owned) {
                f1 = this.owner.submit(() -> {
                    StateRegistry.removeContext(this);
                    this.destroyCurrent();
                });
                this.owned = false;
                this.owner = null;
            }
            if (glfwId != 0) {
                Callbacks.glfwFreeCallbacks(glfwId);
                GLFW.glfwDestroyWindow(this.glfwId);
                glfwId = 0;
            } else {
                logger.error(new Exception("Already destroyed context!!!!"));
            }

            if (!frame.cleaningUp) {
                return f1 == null ? frame.cleanup() : CompletableFuture.allOf(frame.cleanup(), f1);
            }
            return f1 == null ? CompletableFuture.completedFuture(null) : f1;
        }
    }

    GLFWFrame.Creator create(GLFWGLContext shared) {
        GLFWFrame.Creator creator = new GLFWFrame.Creator(frame, shared);
        creator.run();
        this.gl = GLUtil.getGL();
        this.glfwId = creator.glfwId;
        StateRegistry.addContext(this);
        return creator;
    }

    void makeCurrent(boolean force) {
        if (!this.owned || force) {
            this.owned = true;
            this.owner = (ExecutorThread) Thread.currentThread();
            StateRegistry.currentContext(this);
            GLFW.glfwMakeContextCurrent(glfwId);
            GLUtil.createCapabilities();
            gl32().glEnable(GLES32.GL_DEBUG_OUTPUT);
            errorCallback = GLDebugUtil.setupDebugMessageCallback(GLFWGLContext.logger.createPrintStream(GLFWGLContext.level, Logger.LoggerFlags.DONT_PRINT_SOURCE));
        }
    }
}
