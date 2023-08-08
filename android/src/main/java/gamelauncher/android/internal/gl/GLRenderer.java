/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.internal.gl;

import android.opengl.GLSurfaceView;
import android.os.Build;
import androidx.annotation.RequiresApi;
import gamelauncher.android.AndroidGameLauncher;
import gamelauncher.engine.render.FrameRenderer;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ThreadSpecificExecutor;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.states.StateRegistry;
import java8.util.concurrent.CompletableFuture;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GLRenderer implements GLSurfaceView.Renderer {
    private static final Logger logger = Logger.logger();
    public final CompletableFuture<Void> surfaceCreated = new CompletableFuture<>();
    private final AndroidGameLauncher launcher;
    private final AndroidFrame frame;
    private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();
    private FrameRenderer renderer;

    public GLRenderer(AndroidGameLauncher launcher) {
        this.launcher = launcher;
        this.frame = (AndroidFrame) launcher.frame();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1) @Override public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Thread thread = Threads.currentThread();
        thread.setName("AndroidNativeRenderThread");
        ThreadSpecificExecutor executor = new ThreadSpecificExecutor() {
            @Override public Thread thread() {
                return thread;
            }

            @Override public boolean post(Runnable runnable) {
                queue.offer(runnable);
                return true;
            }
        };
        StateRegistry.currentContext(frame.context());

        AndroidNativeRenderThread rt = ((AndroidNativeRenderThread) frame.renderThread());
        rt.executor(executor);
        rt.frame().context().recreate(launcher.egl().eglGetCurrentDisplay(), launcher.egl().eglGetCurrentSurface(EGL10.EGL_DRAW), launcher.egl().eglGetCurrentContext());
        AndroidGLContext.setupDebugMessage();
        if (!surfaceCreated.isDone()) surfaceCreated.complete(null);
    }

    @Override public void onSurfaceChanged(GL10 gl, int width, int height) {
        frame.framebuffer().width().number(width);
        frame.framebuffer().height().number(height);
        updateRenderer();
        if (renderer != null) {
            try {
                renderer.windowSizeChanged(frame);
            } catch (GameException e) {
                launcher.handleError(e);
            }
            return;
        }
        logger.warn("Fallback render");
        frame.context().gl20().glViewport(0, 0, width, height);
    }

    @Override public void onDrawFrame(GL10 gl) {
        loop();
        updateRenderer();
        if (renderer != null) {
            try {
                renderer.renderFrame(frame);
            } catch (GameException e) {
                launcher.handleError(e);
            }
            return;
        }
        logger.warn("Fallback render");
        frame.context().gl20().glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

    private void updateRenderer() {
        FrameRenderer cfr = frame.frameRenderer();
        if (renderer != cfr) {
            if (renderer != null) {
                try {
                    renderer.cleanup(frame);
                } catch (GameException e) {
                    launcher.handleError(e);
                }
            }
            renderer = cfr;
            if (renderer != null) {
                try {
                    renderer.init(frame);
                } catch (GameException e) {
                    launcher.handleError(e);
                }
            }
        }
    }

    private void loop() {
        Runnable r;
        while ((r = queue.poll()) != null) r.run();
    }
}
