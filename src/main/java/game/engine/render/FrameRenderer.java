package game.engine.render;

import static org.lwjgl.glfw.GLFW.*;

public abstract class FrameRenderer {

	public void renderFrame(Window window) {
		renderFrame0(window);
		swapBuffers(window);
	}

	protected abstract void renderFrame0(Window window);

	protected void swapBuffers(Window window) {
		glfwSwapBuffers(window.id.get());
	}
}
