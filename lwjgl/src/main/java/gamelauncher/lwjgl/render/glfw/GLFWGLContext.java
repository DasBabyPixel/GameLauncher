/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl.render.glfw;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.logging.LogColor;
import gamelauncher.engine.util.logging.LogLevel;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.gles.gl.*;
import gamelauncher.gles.states.StateRegistry;
import gamelauncher.lwjgl.render.LWJGLGLES;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengles.GLES;

import java.util.Collection;

public class GLFWGLContext extends AbstractGameResource implements GLContext {

    private static final Logger logger = Logger.logger();

    private static final LogLevel level = new LogLevel("GL", 10, new LogColor(0, 255, 255));
    final Collection<GLFWGLContext> sharedContexts;
    volatile long glfwId;
    GLFWFrame parent;
    private ExecutorThread owner = null;
    private boolean owned = false;
    private GLFWFrame frame;
    private GLES32 gl;

    GLFWGLContext(Collection<GLFWGLContext> sharedContexts) {
        super();
        this.sharedContexts = sharedContexts;
        this.sharedContexts.add(this);
    }

    public long getGLFWId() {
        return this.glfwId;
    }

    GLFWFrame.Creator create(GLFWFrame frame, GLFWGLContext shared) {
        this.frame = frame;
        GLFWFrame.Creator creator = new GLFWFrame.Creator(frame, shared);
        creator.run();
        this.gl = LWJGLGLES.instance;
        this.glfwId = creator.glfwId;
        StateRegistry.addContext(this);
        return creator;
    }

    @Override
    protected void cleanup0() throws GameException {
        sharedContexts.remove(this);
        if (parent != null) parent.contexts.remove(this);
        if (this.owned) {
            Threads.waitFor(this.owner.submit(() -> {
                StateRegistry.removeContext(this);
                this.destroyCurrent();
            }));
            this.owned = false;
            this.owner = null;
        }
        GLFW.glfwDestroyWindow(this.glfwId);
        if (!frame.cleanedUp()) frame.cleanup();
    }

    synchronized void beginCreationShared() throws GameException {
        if (this.owned) {
            Threads.waitFor(this.owner.submit(() -> {
                StateRegistry.currentContext(null);
                GLFW.glfwMakeContextCurrent(0L);
                GLES.setCapabilities(null);
            }));
        }
    }

    synchronized void endCreationShared() throws GameException {
        if (this.owned) {
            Threads.waitFor(this.owner.submit(() -> {
                StateRegistry.currentContext(this);
                GLFW.glfwMakeContextCurrent(glfwId);
                GLES.createCapabilities();
            }));
        }
    }

    public synchronized void destroyCurrent() {
        if (this.owned) {
            StateRegistry.currentContext(null);
            GLFW.glfwMakeContextCurrent(0L);
            GLES.setCapabilities(null);
            this.owned = false;
            this.owner = null;
        }
    }

    @Override
    public GLFWFrame frame() {
        return frame;
    }

    @Override
    public GLFWGLContext createSharedContext() throws GameException {
        GLFWGLContext ctx = new GLFWGLContext(sharedContexts);
        ctx.parent = frame;
        frame.contexts.add(ctx);
        for (GLFWGLContext c : ctx.sharedContexts) {
            c.beginCreationShared();
        }
        Threads.waitFor(frame.launcher().getGLFWThread().submit(() -> {
            GLFWFrame f2 = new GLFWFrame(frame.launcher, ctx);
            ctx.create(f2, this);
            f2.renderThread.start();
        }));
        for (GLFWGLContext c : ctx.sharedContexts) {
            c.endCreationShared();
        }
        return ctx;
    }

    @Override
    public GLES20 gl20() {
        return gl;
    }

    @Override
    public GLES30 gl30() {
        return gl;
    }

    @Override
    public GLES31 gl31() {
        return gl;
    }

    @SuppressWarnings("deprecation")
    @Override
    public GLES32 gl32() {
        return gl;
    }

    @Override
    public synchronized void makeCurrent() {
        if (!this.owned) {
            this.owned = true;
            this.owner = (ExecutorThread) Thread.currentThread();
            StateRegistry.currentContext(this);
            GLFW.glfwMakeContextCurrent(glfwId);
            GLES.createCapabilities();
            //noinspection deprecation
            gl32().glEnable(GLES32.GL_DEBUG_OUTPUT);
            GLUtil.setupDebugMessageCallback(GLFWGLContext.logger.createPrintStream(GLFWGLContext.level));
        }
    }
}
