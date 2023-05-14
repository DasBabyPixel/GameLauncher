/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.gl;

import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import gamelauncher.android.AndroidGameLauncher;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.gl.*;
import gamelauncher.gles.states.StateRegistry;
import java8.util.concurrent.CompletableFuture;

import javax.microedition.khronos.egl.*;
import java.util.Collection;

public class AndroidGLContext extends AbstractGameResource implements GLContext {
    public static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
    public static final int EGL_OPENGL_ES2_BIT = 0x0004;
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
        this.sharedContexts.add(this);
    }

    public AndroidGLContext(Collection<AndroidGLContext> sharedContexts, AndroidGameLauncher launcher, AndroidFrame frame, EGLDisplay display, EGLSurface surface, EGLContext context, GLES32 gl) {
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

    AndroidGLContext create(@Nullable AndroidGLContext context) throws GameException {
        EGL10 egl = launcher.egl();
        EGLDisplay dpy = egl.eglGetDisplay(egl.EGL_DEFAULT_DISPLAY);
        int[] vers = new int[2];
        egl.eglInitialize(dpy, vers);
        int[] configAttr = {egl.EGL_COLOR_BUFFER_TYPE, egl.EGL_RGB_BUFFER, egl.EGL_LEVEL, 0, egl.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT, egl.EGL_SURFACE_TYPE, egl.EGL_PBUFFER_BIT, egl.EGL_NONE};
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfig = new int[1];
        egl.eglChooseConfig(dpy, configAttr, configs, 1, numConfig);
        if (numConfig[0] == 0) {
            throw new GameException("No Config for EGL found");
        }
        EGLConfig config = configs[0];
        int[] surfAttr = {egl.EGL_WIDTH, 1, egl.EGL_HEIGHT, 1, egl.EGL_NONE};
        EGLSurface surf = egl.eglCreatePbufferSurface(dpy, config, surfAttr);
        int[] ctxAttrib = {EGL_CONTEXT_CLIENT_VERSION, 3, egl.EGL_NONE};
        EGLContext ctx = egl.eglCreateContext(dpy, config, context == null ? egl.EGL_NO_CONTEXT : context.context, ctxAttrib);
        this.display = dpy;
        this.surface = surf;
        this.context = ctx;
        this.gl = launcher.glLoader().gles();
        this.frame = new AndroidFrame(launcher, this);
        StateRegistry.addContext(this);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1) @Override protected CompletableFuture<Void> cleanup0() {
        this.sharedContexts.remove(this);
        EGL10 egl = launcher.egl();
        egl.eglDestroySurface(display, surface);
        egl.eglDestroyContext(display, context);
        display = null;
        surface = null;
        context = null;
        return null;
    }

    @Override public void makeCurrent() {
        launcher.egl().eglMakeCurrent(display, surface, surface, context);
        StateRegistry.currentContext(this);
    }

    @Override public void destroyCurrent() {
        StateRegistry.currentContext(null);
        launcher.egl().eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
    }

    @Override public AndroidGLContext createSharedContext() throws GameException {
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

    @Override public GLES32 gl32() {
        return gl;
    }
}
