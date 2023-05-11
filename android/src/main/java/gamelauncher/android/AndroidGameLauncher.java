/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android;

import android.opengl.GLSurfaceView;
import android.os.Build;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.RequiresApi;
import gamelauncher.android.font.AndroidGlyphProvider;
import gamelauncher.android.gl.AndroidGLFactory;
import gamelauncher.android.gl.AndroidGLLoader;
import gamelauncher.android.gl.AndroidMemoryManagement;
import gamelauncher.android.gl.LauncherGLSurfaceView;
import gamelauncher.android.gui.AndroidGuiManager;
import gamelauncher.android.io.AndroidEmbedFileSystemProvider;
import gamelauncher.android.util.AndroidExecutorThreadHelper;
import gamelauncher.android.util.keybind.AndroidKeybindManager;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Frame;
import gamelauncher.engine.resource.SimpleResourceLoader;
import gamelauncher.engine.util.DefaultOperatingSystems;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.GLES;
import gamelauncher.gles.GLESThreadGroup;
import gamelauncher.gles.context.GLESContextProvider;
import gamelauncher.gles.font.bitmap.BasicFontFactory;
import gamelauncher.gles.modelloader.GLESModelLoader;
import gamelauncher.gles.render.GLESGameRenderer;
import gamelauncher.gles.shader.GLESShaderLoader;

import javax.microedition.khronos.egl.EGL10;
import java.io.IOException;
import java.net.URI;

public class AndroidGameLauncher extends GameLauncher {
    private final AndroidGLLoader glLoader;
    private final GLESThreadGroup glThreadGroup;
    private final AndroidLauncher activity;
    private final GLES gles;
    LauncherGLSurfaceView view;
    private volatile boolean keyboardVisible = false;
    private EGL10 egl;

    public AndroidGameLauncher(AndroidLauncher activity) throws GameException {
        this.operatingSystem(DefaultOperatingSystems.ANDROID);
        this.activity = activity;
        this.gameDirectory(activity.getFilesDir().toPath());
        this.glLoader = new AndroidGLLoader();
        gles = new GLES(this, new AndroidMemoryManagement(), new AndroidGLFactory(this));
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
        this.guiManager(new AndroidGuiManager(this));
        this.fontFactory(new BasicFontFactory(gles, this));
        this.textureManager(gles.textureManager());
        gles.init();
        this.glThreadGroup = new GLESThreadGroup();
    }

    @Override protected void start0() throws GameException {
        this.glyphProvider(new AndroidGlyphProvider(gles));
    }

    @Override protected void loadCustomPlugins() {
        activity.init(this);
    }

    @Override public void frame(Frame frame) {
        super.frame(frame);
    }

    public AndroidLauncher activity() {
        return activity;
    }

    public GLSurfaceView view() {
        return view;
    }

    @Override public AndroidKeybindManager keybindManager() {
        return (AndroidKeybindManager) super.keybindManager();
    }

    public GLESThreadGroup glThreadGroup() {
        return glThreadGroup;
    }

    @RequiresApi(api = Build.VERSION_CODES.M) @Override public void keyboardVisible(boolean visible) {
        Runnable r = () -> {
            InputMethodManager manager = activity.getApplicationContext().getSystemService(InputMethodManager.class);
            this.keyboardVisible = visible;
            if (visible) {
                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
                manager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            } else {
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        };
        if (view.getHandler().getLooper().isCurrentThread()) {
            r.run();
        } else {
            view.getHandler().post(r);
        }
    }

    public void egl(EGL10 egl) {
        this.egl = egl;
    }

    public EGL10 egl() {
        return egl;
    }

    public AndroidGLLoader glLoader() {
        return glLoader;
    }

    @Override public boolean keyboardVisible() {
        return keyboardVisible;
    }
}
