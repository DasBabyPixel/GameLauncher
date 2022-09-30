package gamelauncher.lwjgl.render.glfw;

import static org.lwjgl.glfw.GLFW.*;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;

class GLFWWindowContext extends GLFWGLContext {

	private final GLFWWindowCreator creator;

	GLFWWindowContext(GLFWWindow window, GLFWWindowCreator glfwWindowCreator) {
		super(window);
		this.creator = glfwWindowCreator;
	}

	@Override
	protected void applyHints() {
		glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE);
		super.applyHints();
	}

	@Override
	public void cleanup0() throws GameException {
		super.cleanup0();
		Threads.waitFor(creator.contextRender.exit());
	}

}
