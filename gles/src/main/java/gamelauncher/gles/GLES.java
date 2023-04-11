package gamelauncher.gles;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Frame;
import gamelauncher.gles.gl.GLFactory;
import gamelauncher.gles.texture.GLESTextureManager;
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
        this.textureManager = new GLESTextureManager(this);
    }

    public MemoryManagement memoryManagement() {
        return memoryManagement;
    }

    public GLFactory glFactory() {
        return glFactory;
    }

    public GameLauncher launcher() {
        return launcher;
    }

    public GLESTextureManager textureManager() {
        return textureManager;
    }

    public Frame mainFrame() {
        return launcher.frame();
    }

}
