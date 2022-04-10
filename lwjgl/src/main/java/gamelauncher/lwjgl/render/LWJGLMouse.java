package gamelauncher.lwjgl.render;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class LWJGLMouse {

	public final LWJGLWindow window;
	private final AtomicBoolean inWindow = new AtomicBoolean(false);
	private final AtomicReference<Double> x = new AtomicReference<>(0D);
	private final AtomicReference<Double> y = new AtomicReference<>(0D);
	private final AtomicReference<Double> lastx = new AtomicReference<>(0D);
	private final AtomicReference<Double> lasty = new AtomicReference<>(0D);

	public LWJGLMouse(LWJGLWindow window) {
		this.window = window;
	}

	public double getDeltaX() {
		double x = getX();
		return x - lastx.getAndSet(x);
	}

	public double getDeltaY() {
		double y = getY();
		return y - lastx.getAndSet(y);
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
