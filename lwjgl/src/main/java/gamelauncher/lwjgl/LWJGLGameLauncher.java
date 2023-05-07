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
import gamelauncher.engine.resource.SimpleResourceLoader;
import gamelauncher.engine.util.DefaultOperatingSystems;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.KeyboardKeybindEvent;
import gamelauncher.engine.util.keybind.KeyboardKeybindEvent.Type;
import gamelauncher.engine.util.logging.AnsiProvider;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.gles.GLES;
import gamelauncher.gles.GLESThreadGroup;
import gamelauncher.gles.context.GLESContextProvider;
import gamelauncher.gles.font.bitmap.BasicFontFactory;
import gamelauncher.gles.modelloader.GLESModelLoader;
import gamelauncher.gles.render.GLESGameRenderer;
import gamelauncher.gles.shader.GLESShaderLoader;
import gamelauncher.gles.texture.GLESTextureManager;
import gamelauncher.gles.util.MemoryManagement;
import gamelauncher.lwjgl.gui.LWJGLGuiManager;
import gamelauncher.lwjgl.render.LWJGLGLFactory;
import gamelauncher.lwjgl.render.font.bitmap.LWJGLGlyphProvider;
import gamelauncher.lwjgl.render.glfw.GLFWFrame;
import gamelauncher.lwjgl.render.glfw.GLFWThread;
import gamelauncher.lwjgl.render.glfw.GLUtil;
import gamelauncher.lwjgl.settings.controls.MouseSensivityInsertion;
import gamelauncher.lwjgl.util.LWJGLAnsiProvider;
import gamelauncher.lwjgl.util.LWJGLExecutorThreadHelper;
import gamelauncher.lwjgl.util.LWJGLMemoryManagement;
import gamelauncher.lwjgl.util.keybind.LWJGLKeybindManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Configuration;

/**
 * @author DasBabyPixel
 */
public class LWJGLGameLauncher extends GameLauncher {

    private final GLES gles;
    private final GLESThreadGroup glThreadGroup;
    private final MemoryManagement memoryManagement;
    private GLFWFrame mainFrame;
    private GLFWThread glfwThread;

    public LWJGLGameLauncher() throws GameException {
        try {
            this.operatingSystem(DefaultOperatingSystems.LWJGL);
            this.memoryManagement = new LWJGLMemoryManagement();
            this.gles = new GLES(this, this.memoryManagement, new LWJGLGLFactory(this));
            this.executorThreadHelper(new LWJGLExecutorThreadHelper());
            this.contextProvider(new GLESContextProvider(gles, this));
            this.keybindManager(new LWJGLKeybindManager(this));
            this.resourceLoader(new SimpleResourceLoader(this));
            this.shaderLoader(new GLESShaderLoader(gles));
            this.gameRenderer(new GLESGameRenderer(gles));
            this.modelLoader(new GLESModelLoader(gles, this));
            this.guiManager(new LWJGLGuiManager(this));
            this.fontFactory(new BasicFontFactory(gles, this));
            this.textureManager(gles.textureManager());
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
            throw new RuntimeException();
        }
    }

    @EventHandler public void handle(LauncherInitializedEvent event) {
        try {
            Threads.waitFor(this.mainFrame.showWindow());
        } catch (GameException ex) {
            ex.printStackTrace();
        }
    }

    @Override public AnsiProvider ansi() {
        return new LWJGLAnsiProvider();
    }

    @Api @Override protected void tick0() throws GameException {
    }

    @Override protected void start0() throws GameException {
        GLUtil.clinit(this);

        this.glfwThread = new GLFWThread(this);
        this.glfwThread.start();
        Configuration.OPENGL_EXPLICIT_INIT.set(true);
        Configuration.OPENGLES_EXPLICIT_INIT.set(true);
        GL.create();
        org.lwjgl.opengles.GLES.create(GL.getFunctionProvider());
        GL.destroy();

        this.mainFrame = new GLFWFrame(this);
        this.frame(this.mainFrame);
        this.mainFrame.framebuffer().renderThread().submit(() -> this.glyphProvider(new LWJGLGlyphProvider(this)));
        this.mainFrame.renderMode(RenderMode.ON_UPDATE);
        this.mainFrame.closeCallback().value(frame -> {
            this.mainFrame.hideWindow();
            try {
                LWJGLGameLauncher.this.stop();
            } catch (GameException ex) {
                ex.printStackTrace();
            }
        });

        this.eventManager().registerListener(this);

        Keybind keybind = keybindManager().keybind(GLFW.GLFW_KEY_F11);
        keybind.addHandler(entry -> {
            if (entry instanceof KeyboardKeybindEvent) {
                KeyboardKeybindEvent e = (KeyboardKeybindEvent) entry;
                if (e.type() == Type.PRESS) mainFrame.fullscreen().value(!mainFrame.fullscreen().booleanValue());
            }
        });
    }

    @Override protected void stop0() throws GameException {

        this.glyphProvider().cleanup();
        this.textureManager().cleanup();

        this.mainFrame.cleanup();
        Threads.waitFor(this.glfwThread.exit());
    }

    @Override protected void registerSettingInsertions() {
        new MouseSensivityInsertion().register(this);
    }

    @Override public GLESTextureManager textureManager() {
        return (GLESTextureManager) super.textureManager();
    }

    @Override public LWJGLGuiManager guiManager() {
        return (LWJGLGuiManager) super.guiManager();
    }

    public MemoryManagement memoryManagement() {
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

}
