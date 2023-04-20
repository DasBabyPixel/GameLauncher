package gamelauncher.android.gl;

import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import androidx.annotation.RequiresApi;
import gamelauncher.android.AndroidGameLauncher;
import gamelauncher.engine.render.FrameRenderer;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ThreadSpecificExecutor;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.gles.states.StateRegistry;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GLRenderer implements GLSurfaceView.Renderer {
    private static final Logger logger = Logger.logger();
    private final AndroidGameLauncher launcher;
    private final AndroidFrame frame;
    private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();
    private FrameRenderer renderer;
    private ThreadSpecificExecutor executor;

    public GLRenderer(AndroidGameLauncher launcher) {
        this.launcher = launcher;
        this.frame = (AndroidFrame) launcher.frame();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1) @Override public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Thread thread = Thread.currentThread();
        executor = new ThreadSpecificExecutor() {
            @Override public Thread thread() {
                return thread;
            }

            @Override public boolean post(Runnable runnable) {
                queue.offer(runnable);
                return true;
            }
        };
        StateRegistry.currentContext(frame.context());
        logger.info("OpenGL: " + GLES20.glGetString(GLES20.GL_VERSION));
        logger.info("GLSL: " + GLES20.glGetString(GLES20.GL_SHADING_LANGUAGE_VERSION));
        logger.info("Extensions: " + GLES20.glGetString(GLES20.GL_EXTENSIONS));
        AndroidNativeRenderThread rt = ((AndroidNativeRenderThread) frame.renderThread());
        rt.executor(executor);
        rt.frame().context().recreate(EGL14.eglGetCurrentDisplay(), EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW), EGL14.eglGetCurrentContext());
        if (renderer == null) {
            logger.warn("Fallback render");
            GLES20.glClearColor(0.4f, 0.0f, 0.0f, 1.0f);
        }
    }

    @Override public void onSurfaceChanged(GL10 gl, int width, int height) {
        frame.framebuffer().width().setNumber(width);
        frame.framebuffer().height().setNumber(height);
        if (renderer != null) {
            try {
                renderer.windowSizeChanged(frame);
            } catch (GameException e) {
                launcher.handleError(e);
            }
            return;
        }
        logger.warn("Fallback render");
        GLES20.glViewport(0, 0, width, height);
    }

    @Override public void onDrawFrame(GL10 gl) {
        loop();
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
        if (renderer != null) {
            try {
                renderer.renderFrame(frame);
            } catch (GameException e) {
                launcher.handleError(e);
            }
            return;
        }
        logger.warn("Fallback render");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

    private void loop() {
        Runnable r;
        while ((r = queue.poll()) != null) r.run();
    }
}
