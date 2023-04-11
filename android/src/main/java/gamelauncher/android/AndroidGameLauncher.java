/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android;

import android.opengl.GLSurfaceView;
import gamelauncher.android.gl.AndroidGLFactory;
import gamelauncher.android.gl.AndroidMemoryManagement;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.GLES;
import gamelauncher.gles.GLESThreadGroup;
import gamelauncher.gles.context.GLESContextProvider;

public class AndroidGameLauncher extends GameLauncher {
    private final AndroidLauncher activity;
    private final GLES gles;
    private final GLESThreadGroup glThreadGroup;
    private GLSurfaceView view;

    public AndroidGameLauncher(AndroidLauncher activity) {
        this.activity = activity;
        this.glThreadGroup = new GLESThreadGroup();
        this.gles = new GLES(this, new AndroidMemoryManagement(), new AndroidGLFactory(this));
        this.contextProvider(new GLESContextProvider(gles, this));

    }

    @Override
    protected void tick0() throws GameException {

    }

    @Override
    protected void start0() throws GameException {
        view = new GLSurfaceView(activity.getApplicationContext());
        activity.setContentView(view);
    }

    @Override
    protected void stop0() throws GameException {

    }

    public GLSurfaceView view() {
        return view;
    }

    public GLESThreadGroup glThreadGroup() {
        return glThreadGroup;
    }
}
