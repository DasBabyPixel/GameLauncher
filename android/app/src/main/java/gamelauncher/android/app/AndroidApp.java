/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.app;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AndroidApp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView view = new GLSurfaceView(getApplicationContext()) {
            {
                setRenderer(new Renderer() {
                    @Override
                    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                        gl.glClearColor(1, 0, 0, 1);
                    }

                    @Override
                    public void onSurfaceChanged(GL10 gl, int width, int height) {
                        gl.glViewport(0, 0, width, height);
                    }

                    @Override
                    public void onDrawFrame(GL10 gl) {
                        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
                    }
                });
                setRenderMode(RENDERMODE_WHEN_DIRTY);
            }
        };
        setContentView(view);
    }
}
