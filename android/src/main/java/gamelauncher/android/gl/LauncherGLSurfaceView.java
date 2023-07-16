/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.gl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import gamelauncher.android.AndroidGameLauncher;
import gamelauncher.android.AndroidInput;
import gamelauncher.engine.util.logging.Logger;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

@SuppressLint("ViewConstructor")
public class LauncherGLSurfaceView extends GLSurfaceView {
    private static final Logger logger = Logger.logger();
    private final AndroidGameLauncher launcher;

    @SuppressLint("ClickableViewAccessibility") public LauncherGLSurfaceView(AndroidGameLauncher launcher, Context context) {
        super(context);
        this.launcher = launcher;
        AndroidInput input = (AndroidInput) launcher.frame().input();
        setEGLContextFactory(new EGLContextFactory() {
            public static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

            @Override public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
                launcher.egl(egl);
                AndroidFrame frame = (AndroidFrame) launcher.frame();
                frame.context().recreate(egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY), egl.eglGetCurrentSurface(EGL10.EGL_DRAW), egl.eglGetCurrentContext());

                int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, 3, EGL10.EGL_NONE};
                return egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
            }

            @Override public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
                if (!egl.eglDestroyContext(display, context)) {
                    logger.error("Failed to destroy OpenGL ES Context. Display: " + display + ", Context: " + context + ", eglError: " + egl.eglGetError());
                }
            }
        });
//        setEGLConfigChooser(8, 8, 8, 8, 24, 0);
        setEGLConfigChooser(new ConfigChooser(8, 8, 8, 8, 24, 0));
        setRenderer(new GLRenderer(launcher));
        launcher.frame().renderMode();
        updateRenderMode();
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnKeyListener(input);
        setOnTouchListener(input);
        requestFocus();
    }

    @Override public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.imeOptions |= EditorInfo.IME_FLAG_NO_FULLSCREEN;
        return super.onCreateInputConnection(outAttrs);
    }

    public void updateRenderMode() {
        int rm = -1;
        switch (launcher.frame().renderMode()) {
            case MANUAL:
            case CONTINUOUSLY:
                rm = RENDERMODE_CONTINUOUSLY;
                break;
            case ON_UPDATE:
                rm = RENDERMODE_WHEN_DIRTY;
                break;
        }
        setRenderMode(rm);
    }

    private static class ConfigChooser implements GLSurfaceView.EGLConfigChooser {

        /*
         * This EGL config specification is used to specify 2.0 rendering. We use a minimum size of 4 bits for red/green/blue, but
         * will perform actual matching in chooseConfig() below.
         */
        private static final int EGL_OPENGL_ES2_BIT = 4;
        private static final int[] s_configAttribs2 = {EGL10.EGL_RED_SIZE, 4, EGL10.EGL_GREEN_SIZE, 4, EGL10.EGL_BLUE_SIZE, 4, EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT, EGL10.EGL_NONE};
        // Subclasses can adjust these values:
        protected final int mRedSize;
        protected final int mGreenSize;
        protected final int mBlueSize;
        protected final int mAlphaSize;
        protected final int mDepthSize;
        protected final int mStencilSize;
        private final int[] mValue = new int[1];

        public ConfigChooser(int r, int g, int b, int a, int depth, int stencil) {
            mRedSize = r;
            mGreenSize = g;
            mBlueSize = b;
            mAlphaSize = a;
            mDepthSize = depth;
            mStencilSize = stencil;
        }

        @Override public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            /*
             * Get the number of minimally matching EGL configurations
             */
            int[] num_config = new int[1];
            egl.eglChooseConfig(display, s_configAttribs2, null, 0, num_config);

            int numConfigs = num_config[0];

            if (numConfigs <= 0) {
                throw new IllegalArgumentException("No configs match configSpec");
            }

            /*
             * Allocate then read the array of minimally matching EGL configs
             */
            EGLConfig[] configs = new EGLConfig[numConfigs];
            egl.eglChooseConfig(display, s_configAttribs2, configs, numConfigs, num_config);

            /*
             * Now return the "best" one
             */
            //            printConfig(egl, display, cfg);
            return chooseConfig(egl, display, configs);
        }

        private EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
            for (EGLConfig config : configs) {
                int d = findConfigAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE);
                int s = findConfigAttrib(egl, display, config, EGL10.EGL_STENCIL_SIZE);

                // We need at least mDepthSize and mStencilSize bits
                if (d < mDepthSize || s < mStencilSize) continue;

                // We want an *exact* match for red/green/blue/alpha
                int r = findConfigAttrib(egl, display, config, EGL10.EGL_RED_SIZE);
                int g = findConfigAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE);
                int b = findConfigAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE);
                int a = findConfigAttrib(egl, display, config, EGL10.EGL_ALPHA_SIZE);

                if (r == mRedSize && g == mGreenSize && b == mBlueSize && a == mAlphaSize) return config;
            }
            return null;
        }

        private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute) {

            if (egl.eglGetConfigAttrib(display, config, attribute, mValue)) {
                return mValue[0];
            }
            return 0;
        }

//        private void printConfig(EGL10 egl, EGLDisplay display, EGLConfig config) {
//            int[] attributes = {EGL10.EGL_BUFFER_SIZE, EGL10.EGL_ALPHA_SIZE, EGL10.EGL_BLUE_SIZE, EGL10.EGL_GREEN_SIZE, EGL10.EGL_RED_SIZE, EGL10.EGL_DEPTH_SIZE, EGL10.EGL_STENCIL_SIZE, EGL10.EGL_CONFIG_CAVEAT, EGL10.EGL_CONFIG_ID, EGL10.EGL_LEVEL, EGL10.EGL_MAX_PBUFFER_HEIGHT, EGL10.EGL_MAX_PBUFFER_PIXELS, EGL10.EGL_MAX_PBUFFER_WIDTH, EGL10.EGL_NATIVE_RENDERABLE, EGL10.EGL_NATIVE_VISUAL_ID, EGL10.EGL_NATIVE_VISUAL_TYPE, 0x3030, // EGL10.EGL_PRESERVED_RESOURCES,
//                    EGL10.EGL_SAMPLES, EGL10.EGL_SAMPLE_BUFFERS, EGL10.EGL_SURFACE_TYPE, EGL10.EGL_TRANSPARENT_TYPE, EGL10.EGL_TRANSPARENT_RED_VALUE, EGL10.EGL_TRANSPARENT_GREEN_VALUE, EGL10.EGL_TRANSPARENT_BLUE_VALUE, 0x3039, // EGL10.EGL_BIND_TO_TEXTURE_RGB,
//                    0x303A, // EGL10.EGL_BIND_TO_TEXTURE_RGBA,
//                    0x303B, // EGL10.EGL_MIN_SWAP_INTERVAL,
//                    0x303C, // EGL10.EGL_MAX_SWAP_INTERVAL,
//                    EGL10.EGL_LUMINANCE_SIZE, EGL10.EGL_ALPHA_MASK_SIZE, EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RENDERABLE_TYPE, 0x3042 // EGL10.EGL_CONFORMANT
//            };
//            String[] names = {"EGL_BUFFER_SIZE", "EGL_ALPHA_SIZE", "EGL_BLUE_SIZE", "EGL_GREEN_SIZE", "EGL_RED_SIZE", "EGL_DEPTH_SIZE", "EGL_STENCIL_SIZE", "EGL_CONFIG_CAVEAT", "EGL_CONFIG_ID", "EGL_LEVEL", "EGL_MAX_PBUFFER_HEIGHT", "EGL_MAX_PBUFFER_PIXELS", "EGL_MAX_PBUFFER_WIDTH", "EGL_NATIVE_RENDERABLE", "EGL_NATIVE_VISUAL_ID", "EGL_NATIVE_VISUAL_TYPE", "EGL_PRESERVED_RESOURCES", "EGL_SAMPLES", "EGL_SAMPLE_BUFFERS", "EGL_SURFACE_TYPE", "EGL_TRANSPARENT_TYPE", "EGL_TRANSPARENT_RED_VALUE", "EGL_TRANSPARENT_GREEN_VALUE", "EGL_TRANSPARENT_BLUE_VALUE", "EGL_BIND_TO_TEXTURE_RGB", "EGL_BIND_TO_TEXTURE_RGBA", "EGL_MIN_SWAP_INTERVAL", "EGL_MAX_SWAP_INTERVAL", "EGL_LUMINANCE_SIZE", "EGL_ALPHA_MASK_SIZE", "EGL_COLOR_BUFFER_TYPE", "EGL_RENDERABLE_TYPE", "EGL_CONFORMANT"};
//            int[] value = new int[1];
//            for (int i = 0; i < attributes.length; i++) {
//                int attribute = attributes[i];
//                String name = names[i];
//                if (egl.eglGetConfigAttrib(display, config, attribute, value)) {
//                    logger.debugf("  %s: %d\n", name, value[0]);
//                } else {
//                    // Log.w(TAG, String.format("  %s: failed\n", name));
//                    //noinspection StatementWithEmptyBody
//                    while (egl.eglGetError() != EGL10.EGL_SUCCESS) ;
//                }
//            }
//        }
    }
}
