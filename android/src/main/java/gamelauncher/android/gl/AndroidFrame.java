/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.gl;

import android.opengl.EGL14;
import android.os.Handler;
import android.os.Looper;
import de.dasbabypixel.api.property.BooleanValue;
import gamelauncher.android.AndroidGameLauncher;
import gamelauncher.android.AndroidInput;
import gamelauncher.engine.input.Input;
import gamelauncher.engine.render.*;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.framebuffer.ManualQueryFramebuffer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

public class AndroidFrame extends AbstractGameResource implements Frame {
    final ManualQueryFramebuffer manualFramebuffer;
    private final AndroidGameLauncher launcher;
    private final Input input;
    private final AndroidGLContext context;
    private final CompletableFuture<Frame> closeFuture = new CompletableFuture<>();
    private final FrameCounter frameCounter = new FrameCounter();
    private final AndroidFrameFramebuffer framebuffer;
    private final IAndroidRenderThread renderThread;
    private final BooleanValue fullscreen;
    private volatile RenderMode renderMode;
    private volatile FrameRenderer frameRenderer = null;

    public AndroidFrame(AndroidGameLauncher launcher) {
        this.launcher = launcher;
        this.context = new AndroidGLContext(new CopyOnWriteArraySet<>(), launcher, this, EGL14.eglGetCurrentDisplay(), EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW), EGL14.eglGetCurrentContext(), AndroidGLES.instance());
        this.fullscreen = BooleanValue.trueValue();
        this.renderThread = new AndroidNativeRenderThread(this, new Handler(Looper.getMainLooper()));
        this.framebuffer = new AndroidFrameFramebuffer(this);
        this.manualFramebuffer = new ManualQueryFramebuffer(framebuffer);
        this.input = new AndroidInput(launcher);
    }

    AndroidFrame(AndroidGameLauncher launcher, AndroidGLContext context) {
        this.launcher = launcher;
        this.context = context;
        this.fullscreen = BooleanValue.falseValue();
        this.renderThread = new AndroidRenderThread(this);
        this.framebuffer = new AndroidFrameFramebuffer(this);
        this.manualFramebuffer = new ManualQueryFramebuffer(framebuffer);
        this.input = new AndroidInput(launcher);
    }

    @Override
    public Input input() {
        return input;
    }

    @Override
    public CompletableFuture<Frame> frameCloseFuture() {
        return closeFuture;
    }

    @Override
    public Framebuffer framebuffer() {
        return framebuffer;
    }

    @Override
    public RenderMode renderMode() {
        return renderMode;
    }

    @Override
    public void renderMode(RenderMode renderMode) {
        this.renderMode = renderMode;
    }

    @Override
    public void frameRenderer(FrameRenderer renderer) {
        this.frameRenderer = renderer;
    }

    @Override
    public BooleanValue fullscreen() {
        return fullscreen;
    }

    @Override
    public FrameRenderer frameRenderer() {
        return frameRenderer;
    }

    @Override
    public RenderThread renderThread() {
        return renderThread;
    }

    @Override
    public AndroidFrame newFrame() throws GameException {
        return context.createSharedContext().frame();
    }

    public AndroidGLContext context() {
        return context;
    }

    @Override
    public FrameCounter frameCounter() {
        return frameCounter;
    }

    @Override
    public void scheduleDraw() {
        renderThread.scheduleDraw();
    }

    @Override
    public void waitForFrame() {
        renderThread.waitForFrame();
    }

    @Override
    public void scheduleDrawWaitForFrame() {
        renderThread.scheduleDrawWaitForFrame();
    }

    @Override
    public AndroidGameLauncher launcher() {
        return launcher;
    }

    @Override
    protected void cleanup0() throws GameException {
        this.manualFramebuffer.cleanup();
    }
}
