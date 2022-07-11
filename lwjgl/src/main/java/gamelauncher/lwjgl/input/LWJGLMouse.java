package gamelauncher.lwjgl.input;

import static org.lwjgl.glfw.GLFW.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.lwjgl.render.LWJGLWindow;

/**
 * @author DasBabyPixel
 *
 */
public class LWJGLMouse {

	private final LWJGLWindow window;
	private final AtomicBoolean grabbed = new AtomicBoolean(false);
	private final AtomicBoolean inWindow = new AtomicBoolean(false);
	private final AtomicReference<Double> x = new AtomicReference<>(0D);
	private final AtomicReference<Double> y = new AtomicReference<>(0D);
	private final AtomicReference<Double> lastx = new AtomicReference<>(0D);
	private final AtomicReference<Double> lasty = new AtomicReference<>(0D);

	/**
	 * @param window
	 */
	public LWJGLMouse(LWJGLWindow window) {
		this.window = window;
	}

	/**
	 * Sets the grabbed status of the mouse
	 * 
	 * @param grab
	 * @return a completionFuture
	 */
	public CompletableFuture<Void> grabbed(boolean grab) {
		if (grabbed.compareAndSet(!grab, grab)) {
			return window.later(new GameRunnable() {
				@Override
				public void run() {
					glfwSetInputMode(window.getId(), GLFW_CURSOR, getCursorMode());
				}
			});
		}
		return CompletableFuture.completedFuture(null);
	}

	/**
	 * @return if the mouse is grabbed
	 */
	public boolean isGrabbed() {
		return grabbed.get();
	}

	private int getCursorMode() {
		if (grabbed.get()) {
			return GLFW_CURSOR_DISABLED;
		}
		return GLFW_CURSOR_NORMAL;
	}

	/**
	 * @return the delta x since the last call
	 */
	public double getDeltaX() {
		double x = getX();
		return x - lastx.getAndSet(x);
	}

	/**
	 * @return the delta y since the last call
	 */
	public double getDeltaY() {
		double y = getY();
		return y - lasty.getAndSet(y);
	}

	/**
	 * @return the mouse x position
	 */
	public double getX() {
		return x.get();
	}

	/**
	 * @return the mouse y position
	 */
	public double getY() {
		return y.get();
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
