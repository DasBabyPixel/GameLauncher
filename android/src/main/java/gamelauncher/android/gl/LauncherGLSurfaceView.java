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
import android.os.Handler;
import android.os.Looper;
import android.view.inputmethod.InputMethodManager;
import gamelauncher.android.AndroidInput;
import gamelauncher.engine.GameLauncher;

import java.util.concurrent.atomic.AtomicBoolean;

public class LauncherGLSurfaceView extends GLSurfaceView {
    private AndroidInput input;
    private GLRenderer renderer;
    private Handler handler;

    @SuppressLint("ClickableViewAccessibility")
    public LauncherGLSurfaceView(GameLauncher launcher, Context context) {
        super(context);
        handler = new Handler(Looper.myLooper());
        setEGLContextClientVersion(2);
        setRenderer(renderer = new GLRenderer());
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setFocusable(true);
        setFocusableInTouchMode(true);
        AtomicBoolean visible = new AtomicBoolean();
        setOnKeyListener(input);
        setOnTouchListener(input);
        requestFocus();
    }

    public void keyboardVisible(boolean visible) {
        InputMethodManager manager = getContext().getSystemService(InputMethodManager.class);
        if (visible) {
            System.out.println("show");
            setFocusable(true);
            setFocusableInTouchMode(true);
            manager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
        } else {
            System.out.println("hide");
            manager.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }
}
