/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android;

import android.opengl.GLSurfaceView;
import android.os.Handler;
import gamelauncher.android.gl.AndroidGLFactory;
import gamelauncher.android.gl.AndroidMemoryManagement;
import gamelauncher.android.gl.LauncherGLSurfaceView;
import gamelauncher.android.gui.AndroidGuiManager;
import gamelauncher.android.io.AndroidEmbedFileSystemProvider;
import gamelauncher.android.util.AndroidExecutorThreadHelper;
import gamelauncher.android.util.keybind.AndroidKeybindManager;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Frame;
import gamelauncher.engine.resource.SimpleResourceLoader;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.OperatingSystem;
import gamelauncher.gles.GLES;
import gamelauncher.gles.GLESGameRenderer;
import gamelauncher.gles.GLESThreadGroup;
import gamelauncher.gles.context.GLESContextProvider;
import gamelauncher.gles.font.bitmap.BasicFontFactory;
import gamelauncher.gles.modelloader.GLESModelLoader;
import gamelauncher.gles.shader.GLESShaderLoader;

import java.io.IOException;
import java.net.URI;

public class AndroidGameLauncher extends GameLauncher {
    private final AndroidLauncher activity;
    private final Handler mainThread;
    private final GLES gles;
    private final GLESThreadGroup glThreadGroup;
    LauncherGLSurfaceView view;

    public AndroidGameLauncher(AndroidLauncher activity) throws GameException {
        super();
        this.activity = activity;
        this.mainThread = new Handler(activity.getMainLooper());
        this.gameDirectory(activity.getFilesDir().toPath());
        this.gles = new GLES(this, new AndroidMemoryManagement(), new AndroidGLFactory(this));
        this.contextProvider(new GLESContextProvider(gles, this));
        try {
            this.embedFileSystem(new AndroidEmbedFileSystemProvider(activity.getAssets()).newFileSystem((URI) null, null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.executorThreadHelper(new AndroidExecutorThreadHelper(this));
        this.keybindManager(new AndroidKeybindManager(this));
        this.resourceLoader(new SimpleResourceLoader(this));
        this.shaderLoader(new GLESShaderLoader(gles));
        this.gameRenderer(new GLESGameRenderer(gles));
        this.modelLoader(new GLESModelLoader(gles, this));
        this.guiManager(new AndroidGuiManager(this, gles));
        this.fontFactory(new BasicFontFactory(gles, this));
        this.textureManager(gles.textureManager());
        this.operatingSystem(OperatingSystem.ANDROID);
        this.glThreadGroup = new GLESThreadGroup();
    }

    @Override
    public void frame(Frame frame) {
        super.frame(frame);
    }

    @Override
    protected void tick0() throws GameException {
        frame().input().handleInput();
    }

    @Override
    protected void start0() throws GameException {
        System.out.println(130);
    }

    @Override
    protected void stop0() throws GameException {
    }

    public GLSurfaceView view() {
        return view;
    }

    @Override
    public AndroidKeybindManager keybindManager() {
        return (AndroidKeybindManager) super.keybindManager();
    }

    public GLESThreadGroup glThreadGroup() {
        return glThreadGroup;
    }
}
