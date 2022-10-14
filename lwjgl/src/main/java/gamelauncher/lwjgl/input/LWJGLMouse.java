package gamelauncher.lwjgl.input;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.lwjgl.glfw.GLFW;

import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.lwjgl.render.glfw.GLFWFrame;

/**
 * @author DasBabyPixel
 *
 */
public class LWJGLMouse {

	private final GLFWFrame frame;
	private final AtomicBoolean grabbed = new AtomicBoolean(false);
	private final AtomicBoolean inWindow = new AtomicBoolean(false);
	private final AtomicReference<Double> x = new AtomicReference<>(0D);
	private final AtomicReference<Double> y = new AtomicReference<>(0D);
	private final AtomicReference<Double> lastx = new AtomicReference<>(0D);
	private final AtomicReference<Double> lasty = new AtomicReference<>(0D);

	/**
	 * @param frame
	 */
	public LWJGLMouse(GLFWFrame frame) {
		this.frame = frame;
	}

	/**
	 * Sets the grabbed status of the mouse
	 * 
	 * @param grab
	 * @return a completionFuture
	 */
	public CompletableFuture<Void> grabbed(boolean grab) {
		if (this.grabbed.compareAndSet(!grab, grab)) {
			return this.frame.getLauncher().getGLFWThread().submit(new GameRunnable() {
				@Override
				public void run() {
					GLFW.glfwSetInputMode(LWJGLMouse.this.frame.getGLFWId(), GLFW.GLFW_CURSOR, LWJGLMouse.this.getCursorMode());
				}
			});
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
	public double getDeltaX() {
		double x = this.getX();
		return x - this.lastx.getAndSet(x);
	}

	/**
	 * @return the delta y since the last call
	 */
	public double getDeltaY() {
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
	 * @param x
	 * @param y
	 */
	public void setPosition(double x, double y) {
		this.x.set(x);
		this.y.set(y);
	}

	/**
	 * @return true if the mouse is inside the window
	 */
	public boolean isInWindow() {
		return this.inWindow.get();
	}

	/**
	 * Sets if the mouse is inside the window
	 * @param inWindow
	 */
	public void setInWindow(boolean inWindow) {
		this.inWindow.set(inWindow);
	}
}
