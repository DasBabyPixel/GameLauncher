/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.gl;

import android.os.Build;
import androidx.annotation.RequiresApi;
import de.dasbabypixel.api.property.BooleanValue;
import gamelauncher.android.AndroidGameLauncher;
import gamelauncher.android.AndroidInput;
import gamelauncher.engine.input.Input;
import gamelauncher.engine.render.*;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.framebuffer.ManualQueryFramebuffer;
import java8.util.concurrent.CompletableFuture;

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
        this.context = new AndroidGLContext(new CopyOnWriteArraySet<>(), launcher, this, null, null, null, launcher.glLoader().gles());
        this.fullscreen = BooleanValue.trueValue();
        this.renderThread = new AndroidNativeRenderThread(this);
        this.framebuffer = new AndroidFrameFramebuffer(this);
        this.manualFramebuffer = new ManualQueryFramebuffer(framebuffer);
        this.input = new AndroidInput(launcher);
    }

    AndroidFrame(AndroidGameLauncher launcher, AndroidGLContext context) {
        this.launcher = launcher;
        this.context = context;
        this.fullscreen = BooleanValue.falseValue();
        AndroidRenderThread r = new AndroidRenderThread(this);
        this.renderThread = r;
        this.framebuffer = new AndroidFrameFramebuffer(this);
        this.manualFramebuffer = new ManualQueryFramebuffer(framebuffer);
        this.input = new AndroidInput(launcher);
        r.start();
    }

    @Override public Input input() {
        return input;
    }

    @Override public CompletableFuture<Frame> frameCloseFuture() {
        return closeFuture;
    }

    @Override public Framebuffer framebuffer() {
        return framebuffer;
    }

    @Override public RenderMode renderMode() {
        return renderMode;
    }

    @Override public void renderMode(RenderMode renderMode) {
        this.renderMode = renderMode;
    }

    @Override public void frameRenderer(FrameRenderer renderer) {
        if (this == launcher.frame() && renderer != launcher.gameRenderer()) {
            throw new UnsupportedOperationException("Please set the renderer via GameLauncher#gameRenderer to preserve a consistent state");
        }
        this.frameRenderer = renderer;
    }

    @Override public BooleanValue fullscreen() {
        return fullscreen;
    }

    @Override public FrameRenderer frameRenderer() {
        return frameRenderer;
    }

    @Override public RenderThread renderThread() {
        return renderThread;
    }

    @RequiresApi(api = Build.VERSION_CODES.N) @Override public AndroidFrame newFrame() throws GameException {
        return context.createSharedContext().frame();
    }

    public AndroidGLContext context() {
        return context;
    }

    @Override public FrameCounter frameCounter() {
        return frameCounter;
    }

    @Override public void scheduleDraw() {
        renderThread.scheduleDraw();
    }

    @Override public void waitForFrame() {
        renderThread.waitForFrame();
    }

    @Override public void scheduleDrawWaitForFrame() {
        renderThread.scheduleDrawWaitForFrame();
    }

    @Override public AndroidGameLauncher launcher() {
        return launcher;
    }

    @Override protected void cleanup0() throws GameException {
        this.manualFramebuffer.cleanup();
    }
}
