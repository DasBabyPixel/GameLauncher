package gamelauncher.lwjgl.render.glfw.old;

import org.lwjgl.glfw.GLFW;

import gamelauncher.lwjgl.render.framebuffer.AbstractFramebuffer;

/**
 * @author DasBabyPixel
 */
public class WindowFramebuffer extends AbstractFramebuffer {

	private final GLFWFrame frame;

	/**
	 * @param frame
	 */
	public WindowFramebuffer(GLFWFrame frame) {
		super(frame.framebuffer().renderThread(), frame::scheduleDraw);
		this.frame = frame;
	}

	@Override
	public void beginFrame() {
	}

	@Override
	public void endFrame() {
		if (this.frame.swapBuffers) {
			GLFW.glfwSwapBuffers(this.frame.getGLFWId());
		}
	}

}
