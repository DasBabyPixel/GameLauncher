/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.internal.gl;

import android.os.Build;
import androidx.annotation.RequiresApi;
import de.dasbabypixel.annotations.FutureApi;
import gamelauncher.engine.render.FrameRenderer;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.render.RenderThread;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.AbstractExecutorThread;
import gamelauncher.engine.util.concurrent.Phaser;
import gamelauncher.gles.context.GLESStates;
import gamelauncher.gles.framebuffer.ManualQueryFramebuffer;
import gamelauncher.gles.gl.GLES20;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class AndroidRenderThread extends AbstractExecutorThread implements RenderThread, IAndroidRenderThread {
    private static final AtomicInteger ids = new AtomicInteger();
    private final AndroidFrame frame;
    private final AtomicBoolean modifying = new AtomicBoolean(false);
    private final Phaser phaser;
    private final Phaser refreshPhaser;
    private final Consumer<Long> nanoSleeper = this::waitForSignalTimeout;
    private volatile boolean draw = false;
    private volatile boolean drawRefresh = false;
    private volatile boolean refresh = false;
    private ManualQueryFramebuffer fb;
    private FrameRenderer frameRenderer = null;

    AndroidRenderThread(AndroidFrame frame) {
        super(frame.launcher(), frame.launcher().glThreadGroup());
        this.phaser = new Phaser(1);
        this.refreshPhaser = new Phaser(1);

        this.frame = frame;
        this.setName("AndroidRenderThread-" + ids.incrementAndGet());
    }

    @Override protected void startExecuting() {
        this.fb = new ManualQueryFramebuffer(this.frame.framebuffer(), this);
        this.fb.query();
        this.frame.context().makeCurrent();
        AndroidGLContext.setupDebugMessage();

        GLESStates states = new GLESStates();
        states.depth.enabled.value.set(true);
        states.depth.depthFunc.set(GLES20.GL_LEQUAL);
        states.blend.enabled.value.set(true);
        states.blend.srcrgb.set(GLES20.GL_SRC_ALPHA);
        states.blend.dstrgb.set(GLES20.GL_ONE_MINUS_SRC_ALPHA);
        states.replace(null);
    }

    @Override protected void stopExecuting() throws GameException {
        this.fb.cleanup();
        if (this.frameRenderer != null) {
            try {
                this.frameRenderer.cleanup(this.frame);
            } catch (Exception ex) {
                this.frame.launcher().handleError(ex);
            }
        }
        this.frame.launcher().glThreadGroup().terminated(this);
        this.frame.context().destroyCurrent();
    }

    @Override protected void workExecution() {
        this.frame.manualFramebuffer.query();
        if (this.draw || this.frame.renderMode() == RenderMode.CONTINUOUSLY) {
            this.lock();
            boolean refresh = this.drawRefresh;
            if (refresh) this.drawRefresh = false;
            this.draw = false;
            this.unlock();
            this.frame(FrameType.RENDER, !refresh);
            if (refresh) this.frame(FrameType.REFRESH, true);
        }
        if (this.refresh) {
            this.lock();
            this.refresh = false;
            this.unlock();
            this.frame(FrameType.REFRESH, false);
        }
    }

    @Override protected boolean shouldWaitForSignal() {
        return super.shouldWaitForSignal() && this.frame.renderMode() != RenderMode.CONTINUOUSLY;
    }

    private void frame(FrameType type, boolean wait) {
        FrameRenderer cfr = this.frame.frameRenderer();
        cfr:
        if (cfr != null) {
            if (type == FrameType.REFRESH) {
                try {
                    cfr.refreshDisplay(this.frame);
                    this.refreshPhaser.arrive();
                } catch (GameException ex) {
                    this.frame.launcher().handleError(ex);
                }
                break cfr;
            }
            if (this.frameRenderer != cfr) {
                if (this.frameRenderer != null) {
                    try {
                        this.frameRenderer.cleanup(this.frame);
                    } catch (GameException ex) {
                        this.frame.launcher().handleError(ex);
                    }
                }
                this.frameRenderer = cfr;
                try {
                    cfr.init(this.frame);
                } catch (GameException ex) {
                    this.frame.launcher().handleError(ex);
                }
            }

            vp:
            if (this.fb.newValue().booleanValue()) {
                this.fb.query();
                if (this.fb.width().intValue() == 0 || this.fb.height().intValue() == 0) {
                    break vp;
                }
                try {
                    cfr.windowSizeChanged(this.frame);
                } catch (GameException ex) {
                    this.frame.launcher().handleError(ex);
                }
            }
            try {
                cfr.renderFrame(this.frame);
                this.phaser.arrive();
                if (wait) {
                    this.frame.frameCounter().frame(this.nanoSleeper);
                } else {
                    this.frame.frameCounter().frameNoWait();
                }
            } catch (GameException ex) {
                this.frame.launcher().handleError(ex);
            }
        }
    }

    @Override public void scheduleDraw() {
        this.lock();
        this.draw = true;
        this.unlock();
        this.signal();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override public void scheduleDrawWaitForFrame() {
        int frame = this.phaser.getPhase();
        this.scheduleDraw();
        this.phaser.awaitAdvance(frame);
    }

    @Override public void waitForFrame() {
        this.phaser.awaitAdvance(this.phaser.getPhase());
    }

    void refresh() {
        this.lock();
        this.refresh = true;
        this.unlock();
        this.signal();
    }

    @FutureApi void refreshWait() {
        int phase = this.refreshPhaser.getPhase();
        this.refresh();
        this.refreshPhaser.awaitAdvance(phase);
    }

    void scheduleDrawRefresh() {
        this.lock();
        this.drawRefresh = true;
        this.draw = true;
        this.unlock();
        this.signal();
    }

    @FutureApi void scheduleDrawRefreshWait() {
        int drawPhase = this.phaser.getPhase();
        int refreshPhase = this.refreshPhaser.getPhase();
        this.scheduleDrawRefresh();
        this.phaser.awaitAdvance(drawPhase);
        this.refreshPhaser.awaitAdvance(refreshPhase);
    }

    @Override public AndroidFrame frame() {
        return this.frame;
    }

    private void lock() {
        while (!this.modifying.compareAndSet(false, true)) Thread.yield();
    }

    private void unlock() {
        this.modifying.set(false);
    }

    private enum FrameType {
        REFRESH, RENDER
    }

}
