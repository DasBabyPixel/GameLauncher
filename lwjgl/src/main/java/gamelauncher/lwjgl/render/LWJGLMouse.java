package gamelauncher.lwjgl.render;

import static org.lwjgl.glfw.GLFW.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class LWJGLMouse {

	public final LWJGLWindow window;
	private final AtomicBoolean grabbed = new AtomicBoolean(false);
	private final AtomicBoolean inWindow = new AtomicBoolean(false);
	private final AtomicReference<Double> x = new AtomicReference<>(0D);
	private final AtomicReference<Double> y = new AtomicReference<>(0D);
	private final AtomicReference<Double> lastx = new AtomicReference<>(0D);
	private final AtomicReference<Double> lasty = new AtomicReference<>(0D);

	public LWJGLMouse(LWJGLWindow window) {
		this.window = window;
	}

	public CompletableFuture<Void> grabbed(boolean grab) {
		if (grabbed.compareAndSet(!grab, grab)) {
			return window.later(new Runnable() {
				@Override
				public void run() {
					glfwSetInputMode(window.id.get(), GLFW_CURSOR, getCursorMode());
				}
			});
		}
		return CompletableFuture.completedFuture(null);
	}

	public boolean isGrabbed() {
		return grabbed.get();
	}

	private int getCursorMode() {
		if (grabbed.get()) {
			return GLFW_CURSOR_DISABLED;
		}
		return GLFW_CURSOR_NORMAL;
	}

	public double getDeltaX() {
		double x = getX();
		return x - lastx.getAndSet(x);
	}

	public double getDeltaY() {
		double y = getY();
		return y - lasty.getAndSet(y);
	}

	public double getX() {
		return x.get();
	}

	public double getY() {
		return y.get();
	}

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
