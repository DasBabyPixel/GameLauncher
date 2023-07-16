/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.event.EventHandler;
import gamelauncher.engine.event.events.LauncherInitializedEvent;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.render.font.GlyphProvider;
import gamelauncher.engine.resource.SimpleResourceLoader;
import gamelauncher.engine.util.Config;
import gamelauncher.engine.util.Debug;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.KeyboardKeybindEvent;
import gamelauncher.engine.util.keybind.KeyboardKeybindEvent.Type;
import gamelauncher.engine.util.logging.AnsiProvider;
import gamelauncher.engine.util.logging.LogColor;
import gamelauncher.engine.util.logging.LogLevel;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.engine.util.service.ServiceReference;
import gamelauncher.gles.GLES;
import gamelauncher.gles.GLESThreadGroup;
import gamelauncher.gles.context.GLESContextProvider;
import gamelauncher.gles.font.bitmap.BasicFontFactory;
import gamelauncher.gles.modelloader.GLESModelLoader;
import gamelauncher.gles.render.GLESGameRenderer;
import gamelauncher.gles.shader.GLESShaderLoader;
import gamelauncher.gles.texture.GLESTextureManager;
import gamelauncher.lwjgl.gui.LWJGLGuiManager;
import gamelauncher.lwjgl.render.LWJGLGLFactory;
import gamelauncher.lwjgl.render.font.bitmap.LWJGLGlyphProvider;
import gamelauncher.lwjgl.render.glfw.GLFWFrame;
import gamelauncher.lwjgl.render.glfw.GLFWThread;
import gamelauncher.lwjgl.settings.DisplayInsertion;
import gamelauncher.lwjgl.util.LWJGLAnsiProvider;
import gamelauncher.lwjgl.util.LWJGLExecutorThreadHelper;
import gamelauncher.lwjgl.util.LWJGLLoggingProvider;
import gamelauncher.lwjgl.util.LWJGLMemoryManagement;
import gamelauncher.lwjgl.util.image.AWTImageDecoder;
import gamelauncher.lwjgl.util.keybind.LWJGLKeybindManager;
import gamelauncher.netty.NettyNetworkClient;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.Configuration;

/**
 * @author DasBabyPixel
 */
public class LWJGLGameLauncher extends GameLauncher {

    public static final Config<Boolean> REDUCED_LWJGL_LOGGING = Config.createBoolean("reduced_lwjg_logging", true);
    public static final Config<Boolean> USE_GLES = Config.createBoolean("use_gles", true);

    private final GLES gles;
    private final GLESThreadGroup glThreadGroup;
    private final LWJGLMemoryManagement memoryManagement;
    private final SimpleResourceLoader resourceLoader;
    private final LWJGLGuiManager guiManager;
    private final BasicFontFactory fontFactory;
    private final NettyNetworkClient networkClient;
    private final GLESTextureManager textureManager;
    private final LWJGLKeybindManager keybindManager;
    private final GLESShaderLoader shaderLoader;
    private final GLESModelLoader modelLoader;
    private final AWTImageDecoder imageDecoder;
    private final GLFWFrame mainFrame;
    private LWJGLGlyphProvider glyphProvider;
    private GLFWThread glfwThread;

    public LWJGLGameLauncher() {
        super();
        this.serviceProvider().register(ServiceReference.LOGGING_PROVIDER, new LWJGLLoggingProvider());
        super.init();
        if (Debug.debug) {
            Configuration.DEBUG_STREAM.set(Logger.logger("LWJGL").createPrintStream(new LogLevel("LWJGL", 0, LogColor.GREEN, new LogColor(20, 80, 20)), Logger.LoggerFlags.DONT_PRINT_SOURCE));
            Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
            Configuration.DEBUG.set(true);
            Configuration.DEBUG_STACK.set(false);
            Configuration.DEBUG_FUNCTIONS.set(true);
            if (REDUCED_LWJGL_LOGGING.value()) {
                Configuration.DEBUG_FUNCTIONS.set(false);
            }
        } else {
            Configuration.DISABLE_CHECKS.set(true);
        }
        Libraries.init();
        try {
            this.memoryManagement = new LWJGLMemoryManagement();
            this.gles = new GLES(this, this.memoryManagement, new LWJGLGLFactory(this));
            this.executorThreadHelper(new LWJGLExecutorThreadHelper());
            this.contextProvider(new GLESContextProvider(gles, this));
            this.serviceProvider().register(ServiceReference.KEYBIND_MANAGER, keybindManager = new LWJGLKeybindManager(this));
            this.serviceProvider().register(ServiceReference.IMAGE_DECODER, imageDecoder = new AWTImageDecoder());
            this.serviceProvider().register(ServiceReference.RESOURCE_LOADER, resourceLoader = new SimpleResourceLoader());
            this.serviceProvider().register(ServiceReference.SHADER_LOADER, shaderLoader = new GLESShaderLoader(gles));
            this.gameRenderer(new GLESGameRenderer(gles));
            this.serviceProvider().register(ServiceReference.MODEL_LOADER, modelLoader = new GLESModelLoader(gles, this));
            this.serviceProvider().register(ServiceReference.GUI_MANAGER, guiManager = new LWJGLGuiManager(this));
            this.serviceProvider().register(ServiceReference.FONT_FACTORY, fontFactory = new BasicFontFactory(gles, this));
            this.serviceProvider().register(ServiceReference.TEXTURE_MANAGER, textureManager = gles.textureManager());
            this.serviceProvider().register(ServiceReference.NETWORK_CLIENT, networkClient = new NettyNetworkClient(this));
            frame(mainFrame = new GLFWFrame(this));
            gles.init();
            this.glThreadGroup = new GLESThreadGroup();
        } catch (Throwable t) {
            t.printStackTrace();
            try {
                Logger.asyncLogStream().cleanup();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            System.exit(-1);
            throw new Error();
        }
    }

    @EventHandler public void handle(LauncherInitializedEvent event) {
        this.mainFrame.showWindow();
    }

    @Override public AnsiProvider ansi() {
        return LWJGLAnsiProvider.instance();
    }

    @Override public GLESTextureManager textureManager() {
        return (GLESTextureManager) super.textureManager();
    }

    @Override public LWJGLGuiManager guiManager() {
        return (LWJGLGuiManager) super.guiManager();
    }

    public LWJGLMemoryManagement memoryManagement() {
        return memoryManagement;
    }

    @Override public GLFWFrame frame() {
        return (GLFWFrame) super.frame();
    }

    public GLES gles() {
        return gles;
    }

    /**
     * @return the GLFW thread
     */
    public GLFWThread getGLFWThread() {
        return this.glfwThread;
    }

    /**
     * @return the {@link GLESThreadGroup}
     */
    public GLESThreadGroup glThreadGroup() {
        return this.glThreadGroup;
    }

    @Api @Override protected void tick0() throws GameException {
    }

    @Override protected void start0() throws GameException {
        this.glfwThread = new GLFWThread(this);
        this.glfwThread.start();
        Threads.await(glfwThread.submit(() -> null));

        serviceProvider().register(GlyphProvider.class, glyphProvider = new LWJGLGlyphProvider(this));
        this.networkClient().start();
        mainFrame.renderMode(RenderMode.ON_UPDATE);
        mainFrame.closeCallback().value(frame -> {
            mainFrame.hideWindow();
            try {
                stop();
            } catch (GameException ex) {
                ex.printStackTrace();
            }
        });

        this.eventManager().registerListener(this);

        Keybind keybind = keybindManager().keybind(GLFW.GLFW_KEY_F11);
        keybind.addHandler(entry -> {
            if (entry instanceof KeyboardKeybindEvent) {
                KeyboardKeybindEvent e = (KeyboardKeybindEvent) entry;
                if (e.type() != Type.PRESS) return;
                mainFrame.fullscreen().value(!mainFrame.fullscreen().booleanValue());
            }
        });
        mainFrame.startMainFrame();
    }

    @Override protected void stop0() throws GameException {
        Threads.await(guiManager.cleanup());
        Threads.await(networkClient.cleanup());
        Threads.await(glyphProvider.cleanup());
        Threads.await(textureManager.cleanup());

        Threads.await(mainFrame.cleanup());
        Threads.await(glfwThread.exit());
        Threads.await(keybindManager.cleanup());
        Threads.await(resourceLoader.cleanup());
        Threads.await(imageDecoder.cleanup());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            int i = memoryManagement.count.get();
            if (i != 0) System.out.println("Memory Alloc Leak Count: " + i);
        }));
    }

    @Override protected void registerSettingInsertions() {
        new DisplayInsertion().register(this);
    }

}
