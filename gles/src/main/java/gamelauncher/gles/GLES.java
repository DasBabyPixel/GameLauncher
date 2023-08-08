package gamelauncher.gles;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.guis.ColorGui;
import gamelauncher.engine.gui.guis.LineGui;
import gamelauncher.engine.gui.guis.TextureGui;
import gamelauncher.engine.render.ContextProvider;
import gamelauncher.engine.render.Frame;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.service.ServiceReference;
import gamelauncher.gles.context.GLESContextProvider;
import gamelauncher.gles.gl.GLFactory;
import gamelauncher.gles.gui.GLESColorGui;
import gamelauncher.gles.gui.GLESGuiConstructorTemplates;
import gamelauncher.gles.gui.GLESLineGui;
import gamelauncher.gles.gui.GLESTextureGui;
import gamelauncher.gles.mesh.MeshGLDataFactory;
import gamelauncher.gles.render.MeshRenderer;
import gamelauncher.gles.texture.GLESTextureManager;
import gamelauncher.gles.util.GLSectionHandler;
import gamelauncher.gles.util.MemoryManagement;

public class GLES {

    public static final ServiceReference<GLES> GLES = new ServiceReference<>(GLES.class);
    public static final ServiceReference<MeshGLDataFactory> MESH_GL_DATA_FACTORY = new ServiceReference<>(MeshGLDataFactory.class);
    public static final ServiceReference<MeshRenderer> MESH_RENDERER = new ServiceReference<>(MeshRenderer.class);

    private final GameLauncher launcher;
    private final MemoryManagement memoryManagement;
    private final GLFactory glFactory;
    private final GLESTextureManager textureManager;

    public GLES(GameLauncher launcher, MemoryManagement memoryManagement, GLFactory glFactory) throws GameException {
        this.launcher = launcher;
        this.memoryManagement = memoryManagement;
        this.glFactory = glFactory;
        this.textureManager = new GLESTextureManager(launcher, this);
        launcher.profiler().addHandler("render", new GLSectionHandler());
        launcher.serviceProvider().register(GLES, this);
        launcher.serviceProvider().register(MESH_GL_DATA_FACTORY, new MeshGLDataFactory.LazyInit(this));
        launcher.serviceProvider().register(MESH_RENDERER, new MeshRenderer(this));
        launcher.serviceProvider().register(ServiceReference.CONTEXT_PROVIDER, new GLESContextProvider(this, launcher));
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
        launcher.guiManager().registerGuiCreator(null, LineGui.class, GLESLineGui.class);
    }
}
