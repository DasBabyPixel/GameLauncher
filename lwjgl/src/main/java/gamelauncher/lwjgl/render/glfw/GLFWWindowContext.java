package gamelauncher.lwjgl.render.glfw;

import static org.lwjgl.glfw.GLFW.*;

class GLFWWindowContext extends GLFWGLContext {

	GLFWWindowContext(GLFWWindow window) {
		super(window);
	}

	@Override
	protected void applyHints() {
		glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE);
		super.applyHints();
	}
}
