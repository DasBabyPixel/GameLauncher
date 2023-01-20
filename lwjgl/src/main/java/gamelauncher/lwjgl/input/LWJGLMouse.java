package gamelauncher.lwjgl.input;

import gamelauncher.lwjgl.render.glfw.GLFWFrame;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author DasBabyPixel
 */
public class LWJGLMouse {

	private final GLFWFrame frame;
	private final AtomicBoolean grabbed = new AtomicBoolean(false);
	private final AtomicBoolean inWindow = new AtomicBoolean(false);
	private final AtomicReference<Double> x = new AtomicReference<>(0D);
	private final AtomicReference<Double> y = new AtomicReference<>(0D);
	private final AtomicReference<Double> lastx = new AtomicReference<>(0D);
	private final AtomicReference<Double> lasty = new AtomicReference<>(0D);

	public LWJGLMouse(GLFWFrame frame) {
		this.frame = frame;
	}

	/**
	 * Sets the grabbed status of the mouse
	 *
	 * @param grab whether to grab the mouse
	 *
	 * @return a completionFuture
	 */
	public CompletableFuture<Void> grabbed(boolean grab) {
		if (this.grabbed.compareAndSet(!grab, grab)) {
			return this.frame.launcher().getGLFWThread()
					.submit(() -> GLFW.glfwSetInputMode(LWJGLMouse.this.frame.getGLFWId(),
							GLFW.GLFW_CURSOR, LWJGLMouse.this.getCursorMode()));
		}
		return CompletableFuture.completedFuture(null);
	}

	/**
	 * @return if the mouse is grabbed
	 */
	public boolean isGrabbed() {
		return this.grabbed.get();
	}

	private int getCursorMode() {
		if (this.grabbed.get()) {
			return GLFW.GLFW_CURSOR_DISABLED;
		}
		return GLFW.GLFW_CURSOR_NORMAL;
	}

	/**
	 * @return the delta x since the last call
	 */
	public double deltaX() {
		double x = this.getX();
		return x - this.lastx.getAndSet(x);
	}

	/**
	 * @return the delta y since the last call
	 */
	public double deltaY() {
		double y = this.getY();
		return y - this.lasty.getAndSet(y);
	}

	/**
	 * @return the mouse x position
	 */
	public double getX() {
		return this.x.get();
	}

	/**
	 * @return the mouse y position
	 */
	public double getY() {
		return this.y.get();
	}

	/**
	 * Sets the mouse position
	 *
	 * @param x the new x position
	 * @param y the new y position
	 */
	public void setPosition(double x, double y) {
		this.x.set(x);
		this.y.set(y);
	}

	public boolean isInWindow() {
		return this.inWindow.get();
	}

	public void setInWindow(boolean inWindow) {
		this.inWindow.set(inWindow);
	}
}
