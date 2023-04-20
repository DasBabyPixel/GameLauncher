/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.gl;

import android.opengl.*;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import gamelauncher.android.AndroidGameLauncher;
import gamelauncher.android.gl.supported.SupportedAndroidGLES32;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.logging.LogColor;
import gamelauncher.engine.util.logging.LogLevel;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.gl.GLES30;
import gamelauncher.gles.gl.GLES31;
import gamelauncher.gles.gl.GLES32;
import gamelauncher.gles.gl.*;
import gamelauncher.gles.states.StateRegistry;

import java.util.Collection;

public class AndroidGLContext extends AbstractGameResource implements GLContext {
    public static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
    private static final Logger logger = Logger.logger();
    private static final LogLevel level = new LogLevel("GL", 10, new LogColor(0, 255, 255));

    private final Collection<AndroidGLContext> sharedContexts;
    private final AndroidGameLauncher launcher;
    private AndroidFrame frame;
    private EGLDisplay display;
    private EGLSurface surface;
    private EGLContext context;
    private GLES31 gl;

    public AndroidGLContext(AndroidGameLauncher launcher, Collection<AndroidGLContext> sharedContexts) {
        this.launcher = launcher;
        this.sharedContexts = sharedContexts;
        this.sharedContexts.add(this);
    }

    public AndroidGLContext(Collection<AndroidGLContext> sharedContexts, AndroidGameLauncher launcher, AndroidFrame frame, EGLDisplay display, EGLSurface surface, EGLContext context, GLES31 gl) {
        this.sharedContexts = sharedContexts;
        this.sharedContexts.add(this);
        this.launcher = launcher;
        this.frame = frame;
        this.display = display;
        this.surface = surface;
        this.context = context;
        this.gl = gl;
        StateRegistry.addContext(this);
    }

    public void recreate(EGLDisplay display, EGLSurface surface, EGLContext context) {
        this.display = display;
        this.surface = surface;
        this.context = context;
    }

    @Override public AndroidFrame frame() {
        return frame;
    }

    @RequiresApi(api = Build.VERSION_CODES.N) AndroidGLContext create(@Nullable AndroidGLContext context) throws GameException {
        EGLDisplay dpy = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        int[] vers = new int[2];
        EGL14.eglInitialize(dpy, vers, 0, vers, 1);
        int[] configAttr = {EGL14.EGL_COLOR_BUFFER_TYPE, EGL14.EGL_RGB_BUFFER, EGL14.EGL_LEVEL, 0, EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT, EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT, EGL14.EGL_NONE};
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfig = new int[1];
        EGL14.eglChooseConfig(dpy, configAttr, 0, configs, 0, 1, numConfig, 0);
        if (numConfig[0] == 0) {
            throw new GameException("No Config for EGL found");
        }
        EGLConfig config = configs[0];
        int[] surfAttr = {EGL14.EGL_WIDTH, 1, EGL14.EGL_HEIGHT, 1, EGL14.EGL_NONE};
        EGLSurface surf = EGL14.eglCreatePbufferSurface(dpy, config, surfAttr, 0);
        int[] ctxAttrib = {EGL_CONTEXT_CLIENT_VERSION, 3, EGL14.EGL_NONE};
        EGLContext ctx = EGL14.eglCreateContext(dpy, config, context == null ? EGL14.EGL_NO_CONTEXT : context.context, ctxAttrib, 0);
        this.display = dpy;
        this.surface = surf;
        this.context = ctx;
        this.gl = new SupportedAndroidGLES32();
        this.frame = new AndroidFrame(launcher, this);
        StateRegistry.addContext(this);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1) @Override protected void cleanup0() {
        this.sharedContexts.remove(this);
        EGL14.eglDestroySurface(display, surface);
        EGL14.eglDestroyContext(display, context);
        display = null;
        surface = null;
        context = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1) @Override public void makeCurrent() {
        EGL14.eglMakeCurrent(display, surface, surface, context);
        StateRegistry.currentContext(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1) @Override public void destroyCurrent() {
        StateRegistry.currentContext(null);
        EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
    }

    @RequiresApi(api = Build.VERSION_CODES.N) @Override public AndroidGLContext createSharedContext() throws GameException {
        return new AndroidGLContext(launcher, sharedContexts).create(this);
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

    @Override @Deprecated public GLES32 gl32() {
        return null;
    }
}
