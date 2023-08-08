/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android;

import gamelauncher.android.internal.font.AndroidGlyphProvider;
import gamelauncher.android.internal.gl.AndroidGLFactory;
import gamelauncher.android.internal.gl.AndroidGLLoader;
import gamelauncher.android.internal.gl.AndroidMemoryManagement;
import gamelauncher.android.internal.gl.LauncherGLSurfaceView;
import gamelauncher.android.internal.gui.AndroidGuiManager;
import gamelauncher.android.internal.io.AndroidEmbedFileSystemProvider;
import gamelauncher.android.internal.util.*;
import gamelauncher.android.internal.util.keybind.AndroidKeybindManager;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.data.GameDirectoryResolver;
import gamelauncher.engine.gui.GuiManager;
import gamelauncher.engine.network.NetworkClient;
import gamelauncher.engine.render.Frame;
import gamelauncher.engine.render.font.GlyphProvider;
import gamelauncher.engine.render.texture.TextureManager;
import gamelauncher.engine.resource.ResourceLoader;
import gamelauncher.engine.resource.SimpleResourceLoader;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.image.ImageDecoder;
import gamelauncher.engine.util.keybind.KeybindManager;
import gamelauncher.engine.util.service.ServiceReference;
import gamelauncher.gles.GLES;
import gamelauncher.gles.GLESThreadGroup;
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
    public static final ServiceReference<AndroidGameLauncher> ANDROID_GAME_LAUNCHER = new ServiceReference<>(AndroidGameLauncher.class);
    public static final ServiceReference<EGL10> EGL10 = new ServiceReference<>(EGL10.class);
    private final GLESThreadGroup glThreadGroup;
    private final AndroidLauncher activity;
    private final GLES gles;
    private final KeybindManager keybindManager;
    private final ResourceLoader resourceLoader;
    private final GuiManager guiManager;
    private final TextureManager textureManager;
    private final NetworkClient networkClient;
    private final ImageDecoder imageDecoder;
    private final KeyboardUtils keyboardUtils;
    LauncherGLSurfaceView view;
    private GlyphProvider glyphProvider;
    private volatile boolean keyboardVisible = false;

    public AndroidGameLauncher(AndroidLauncher activity) throws GameException {
        serviceProvider().register(ANDROID_GAME_LAUNCHER, this);
        this.activity = activity;
        serviceProvider().register(ServiceReference.LOGGING_PROVIDER, new AndroidLoggingProvider());
        serviceProvider().register(GameDirectoryResolver.AndroidProvider.class, () -> activity.getFilesDir().toPath());
        init();
        serviceProvider().register(AndroidGLLoader.ANDROID_GL_LOADER, new AndroidGLLoader(activity));
        gles = new GLES(this, new AndroidMemoryManagement(), new AndroidGLFactory(this));

        serviceProvider().register(ServiceReference.EXECUTOR_THREAD_HELPER, new AndroidExecutorThreadHelper(this));
        serviceProvider().register(ServiceReference.KEYBIND_MANAGER, keybindManager = new AndroidKeybindManager(this));
        serviceProvider().register(ServiceReference.RESOURCE_LOADER, resourceLoader = new SimpleResourceLoader());
        serviceProvider().register(ServiceReference.SHADER_LOADER, new GLESShaderLoader(gles));
        renderer(new GLESGameRenderer(gles));
        serviceProvider().register(ServiceReference.MODEL_LOADER, new GLESModelLoader(gles, this));
        serviceProvider().register(ServiceReference.GUI_MANAGER, guiManager = new AndroidGuiManager(this));
        serviceProvider().register(ServiceReference.FONT_FACTORY, new BasicFontFactory(gles, this));
        serviceProvider().register(ServiceReference.TEXTURE_MANAGER, textureManager = gles.textureManager());
        serviceProvider().register(ServiceReference.NETWORK_CLIENT, networkClient = new NettyNetworkClient(this));
        serviceProvider().register(ServiceReference.IMAGE_DECODER, imageDecoder = new DumbImageDecoder());
        gles.init();
        this.glThreadGroup = new GLESThreadGroup();
        this.keyboardUtils = new KeyboardUtils(activity, isVisible -> {
            keyboardVisible = isVisible;
            if (!keyboardVisible) threads().cached.submit(() -> activity.runOnUiThread(() -> immersiveMode().update()));
        });
    }

    @Override public void frame(Frame frame) {
        super.frame(frame);
    }

    public AndroidLauncher activity() {
        return activity;
    }

    public LauncherGLSurfaceView view() {
        return view;
    }

    @Override public AndroidKeybindManager keybindManager() {
        return (AndroidKeybindManager) super.keybindManager();
    }

    /**
     * @return the threadgroup other threads are started on
     */
    public GLESThreadGroup glThreadGroup() {
        return glThreadGroup;
    }

    @Override public void keyboardVisible(boolean visible) {
        activity.runOnUiThread(() -> {
            if (visible) {
                view.setFocusableInTouchMode(true);
                KeyboardUtils.forceShowKeyboard(view);
            } else KeyboardUtils.forceCloseKeyboard(view);
        });
    }

    public EGL10 egl() {
        return serviceProvider().service(EGL10);
    }

    @Override public boolean keyboardVisible() {
        return keyboardVisible;
    }

    public ImmersiveMode immersiveMode() {
        return serviceProvider().service(ImmersiveMode.IMMERSIVE_MODE);
    }

    @Override protected void stop0() throws GameException {
        Threads.await(guiManager.cleanup());
        Threads.await(networkClient.cleanup());
        Threads.await(glyphProvider.cleanup());
        Threads.await(textureManager.cleanup());

        Threads.await(keybindManager.cleanup());
        Threads.await(resourceLoader.cleanup());
        Threads.await(imageDecoder.cleanup());
        super.stop0();
    }

    @Override protected FileSystem createEmbedFileSystem() throws IOException {
        return new AndroidEmbedFileSystemProvider(activity.getAssets()).newFileSystem((URI) null, null);
    }

    @Override protected void start0() throws GameException {
        serviceProvider().register(ImmersiveMode.IMMERSIVE_MODE, new ImmersiveMode(this));
        serviceProvider().register(ServiceReference.GLYPH_PROVIDER, glyphProvider = new AndroidGlyphProvider(gles));
        networkClient.start();
        Threads.await(view.initialized);
        Threads.await(view.glRenderer.surfaceCreated);
    }

    @Override protected void loadCustomPlugins() {
        activity.init(this);
    }
}
