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
import gamelauncher.engine.data.GameDirectoryResolver;
import gamelauncher.engine.gui.GuiManager;
import gamelauncher.engine.network.NetworkClient;
import gamelauncher.engine.render.Frame;
import gamelauncher.engine.render.font.FontFactory;
import gamelauncher.engine.render.font.GlyphProvider;
import gamelauncher.engine.render.model.ModelLoader;
import gamelauncher.engine.render.shader.ShaderLoader;
import gamelauncher.engine.render.texture.TextureManager;
import gamelauncher.engine.resource.ResourceLoader;
import gamelauncher.engine.resource.SimpleResourceLoader;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.keybind.KeybindManager;
import gamelauncher.engine.util.service.ServiceReference;
import gamelauncher.gles.GLES;
import gamelauncher.gles.GLESThreadGroup;
import gamelauncher.gles.context.GLESContextProvider;
import gamelauncher.gles.font.bitmap.BasicFontFactory;
import gamelauncher.gles.modelloader.GLESModelLoader;
import gamelauncher.gles.render.GLESGameRenderer;
import gamelauncher.gles.shader.GLESShaderLoader;
import gamelauncher.netty.NettyNetworkClient;

import javax.microedition.khronos.egl.EGL10;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;

public class AndroidGameLauncher extends GameLauncher {
    private final AndroidGLLoader glLoader;
    private final GLESThreadGroup glThreadGroup;
    private final AndroidLauncher activity;
    private final GLES gles;
    private final KeybindManager keybindManager;
    private final ResourceLoader resourceLoader;
    private final ShaderLoader shaderLoader;
    private final ModelLoader modelLoader;
    private final GuiManager guiManager;
    private final TextureManager textureManager;
    private final FontFactory fontFactory;
    private final NetworkClient networkClient;
    LauncherGLSurfaceView view;
    private GlyphProvider glyphProvider;
    private volatile boolean keyboardVisible = false;
    private EGL10 egl;

    public AndroidGameLauncher(AndroidLauncher activity) throws GameException {
        this.activity = activity;
        serviceProvider().register(GameDirectoryResolver.AndroidProvider.class, () -> activity.getFilesDir().toPath());
        init();
        this.glLoader = new AndroidGLLoader();
        gles = new GLES(this, new AndroidMemoryManagement(), new AndroidGLFactory(this));
        this.contextProvider(new GLESContextProvider(gles, this));

        this.executorThreadHelper(new AndroidExecutorThreadHelper(this));
        serviceProvider().register(ServiceReference.KEYBIND_MANAGER, keybindManager = new AndroidKeybindManager(this));
        serviceProvider().register(ServiceReference.RESOURCE_LOADER, resourceLoader = new SimpleResourceLoader());
        serviceProvider().register(ServiceReference.SHADER_LOADER, shaderLoader = new GLESShaderLoader(gles));
        this.gameRenderer(new GLESGameRenderer(gles));
        serviceProvider().register(ServiceReference.MODEL_LOADER, modelLoader = new GLESModelLoader(gles, this));
        serviceProvider().register(ServiceReference.GUI_MANAGER, guiManager = new AndroidGuiManager(this));
        serviceProvider().register(ServiceReference.FONT_FACTORY, fontFactory = new BasicFontFactory(gles, this));
        serviceProvider().register(ServiceReference.TEXTURE_MANAGER, textureManager = gles.textureManager());
        serviceProvider().register(ServiceReference.NETWORK_CLIENT, networkClient = new NettyNetworkClient(this));
        gles.init();
        this.glThreadGroup = new GLESThreadGroup();
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

    @Override protected void stop0() throws GameException {
        Threads.await(guiManager.cleanup());
        Threads.await(networkClient.cleanup());
        Threads.await(glyphProvider.cleanup());
        Threads.await(textureManager.cleanup());

        Threads.await(keybindManager.cleanup());
        Threads.await(resourceLoader.cleanup());
        super.stop0();
    }

    @Override protected FileSystem createEmbedFileSystem() throws IOException {
        return new AndroidEmbedFileSystemProvider(activity.getAssets()).newFileSystem((URI) null, null);
    }

    @Override protected void start0() throws GameException {
        serviceProvider().register(ServiceReference.GLYPH_PROVIDER, glyphProvider = new AndroidGlyphProvider(gles));
    }

    @Override protected void loadCustomPlugins() {
        activity.init(this);
    }
}
