package gamelauncher.lwjgl.render;

import gamelauncher.engine.util.GameException;
import gamelauncher.gles.gl.GLFactory;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.glfw.GLFWFrame;
import gamelauncher.lwjgl.render.glfw.GLFWGLContext;

public class LWJGLGLFactory implements GLFactory {
    private final LWJGLGameLauncher launcher;

    public LWJGLGLFactory(LWJGLGameLauncher launcher) {
        this.launcher = launcher;
    }

    @Override public GLFWGLContext createContext() throws GameException {
        GLFWFrame glfwFrame = new GLFWFrame(launcher);
        return glfwFrame.context();
    }
}
