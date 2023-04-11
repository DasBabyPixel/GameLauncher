/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.gl;

import android.opengl.*;
import androidx.annotation.Nullable;
import gamelauncher.android.AndroidGameLauncher;
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

import java.util.Collection;

public class AndroidGLContext extends AbstractGameResource implements GLContext {
    private static final Logger logger = Logger.logger();
    private static final LogLevel level = new LogLevel("GL", 10, new LogColor(0, 255, 255));

    private final Collection<AndroidGLContext> sharedContexts;
    private final AndroidGameLauncher launcher;
    private AndroidFrame frame;
    private EGLDisplay display;
    private EGLSurface surface;
    private EGLContext context;
    private GLES32 gl;

    public AndroidGLContext(AndroidGameLauncher launcher, Collection<AndroidGLContext> sharedContexts) {
        this.launcher = launcher;
        this.sharedContexts = sharedContexts;
    }

    public AndroidGLContext(Collection<AndroidGLContext> sharedContexts, AndroidGameLauncher launcher, AndroidFrame frame, EGLDisplay display, EGLSurface surface, EGLContext context, GLES32 gl) {
        this.sharedContexts = sharedContexts;
        this.launcher = launcher;
        this.frame = frame;
        this.display = display;
        this.surface = surface;
        this.context = context;
        this.gl = gl;
    }

    @Override
    public AndroidFrame frame() {
        return frame;
    }

    AndroidGLContext create(@Nullable AndroidGLContext context) throws GameException {
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
        int[] ctxAttrib = {EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE};
        EGLContext ctx = EGL14.eglCreateContext(dpy, config, EGL14.EGL_NO_CONTEXT, ctxAttrib, 0);
        this.display = dpy;
        this.surface = surf;
        this.context = ctx;
        this.gl = AndroidGLES.instance();
        return this;
    }

    @Override
    protected void cleanup0() {
        EGL14.eglDestroySurface(display, surface);
        EGL14.eglDestroyContext(display, context);
        display = null;
        surface = null;
        context = null;
    }

    @Override
    public void makeCurrent() {
        EGL14.eglMakeCurrent(display, surface, surface, context);
    }

    @Override
    public void destroyCurrent() {
        EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
    }

    @Override
    public AndroidGLContext createSharedContext() throws GameException {
        return new AndroidGLContext(launcher, sharedContexts).create(this);
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

    @Override
    public GLES32 gl32() {
        return gl;
    }
}
