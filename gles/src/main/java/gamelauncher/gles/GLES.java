package gamelauncher.gles;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.guis.ColorGui;
import gamelauncher.engine.gui.guis.TextureGui;
import gamelauncher.engine.render.Frame;
import gamelauncher.gles.gl.GLFactory;
import gamelauncher.gles.gui.GLESColorGui;
import gamelauncher.gles.gui.GLESGuiConstructorTemplates;
import gamelauncher.gles.gui.GLESTextureGui;
import gamelauncher.gles.mesh.MeshGLDataFactory;
import gamelauncher.gles.render.MeshRenderer;
import gamelauncher.gles.texture.GLESTextureManager;
import gamelauncher.gles.util.GLSectionHandler;
import gamelauncher.gles.util.MemoryManagement;

public class GLES {

    private final GameLauncher launcher;
    private final MemoryManagement memoryManagement;
    private final GLFactory glFactory;
    private final GLESTextureManager textureManager;

    public GLES(GameLauncher launcher, MemoryManagement memoryManagement, GLFactory glFactory) {
        this.launcher = launcher;
        this.memoryManagement = memoryManagement;
        this.glFactory = glFactory;
        this.textureManager = new GLESTextureManager(launcher, this);
        launcher.profiler().addHandler("render", new GLSectionHandler());
        launcher.serviceProvider().register(MeshGLDataFactory.class, new MeshGLDataFactory.FactoryGL30(this));
        launcher.serviceProvider().register(MeshRenderer.class, new MeshRenderer(this));
        GLESGuiConstructorTemplates.init(this);
    }

    @Api public MemoryManagement memoryManagement() {
        return memoryManagement;
    }

    @Api public GLFactory glFactory() {
        return glFactory;
    }

    @Api public GameLauncher launcher() {
        return launcher;
    }

    @Api public GLESTextureManager textureManager() {
        return textureManager;
    }

    @Api public Frame mainFrame() {
        return launcher.frame();
    }

    @Api public void init() {
        launcher.guiManager().registerGuiCreator(null, TextureGui.class, GLESTextureGui.class);
        launcher.guiManager().registerGuiCreator(null, ColorGui.class, GLESColorGui.class);
    }
}
