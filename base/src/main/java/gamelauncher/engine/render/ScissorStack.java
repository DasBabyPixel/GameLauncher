package gamelauncher.engine.render;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import gamelauncher.engine.util.math.Math;

/**
 * @author DasBabyPixel
 */
public abstract class ScissorStack {

	private final Deque<Scissor> stack = new ConcurrentLinkedDeque<>();

	/**
	 */
	public ScissorStack() {
	}

	/**
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public void pushScissor(int x, int y, int w, int h) {
		Scissor last = stack.peekLast();
		if (last != null) {
			x = Math.max(x, last.x);
			y = Math.max(y, last.y);
			w = Math.min(w, last.w + last.x - x);
			h = Math.min(h, last.h + last.y - y);
		}
		if (w < 0) {
			w = 0;
		}
		if (h < 0) {
			h = 0;
		}
		if (stack.isEmpty()) {
			enableScissor();
		}
		stack.addLast(new Scissor(x, y, w, h));
		setScissor(last());
	}

	/**
	 * 
	 */
	public void popScissor() {
		stack.removeLast();
		if (stack.isEmpty()) {
			disableScissor();
		}
	}

	/**
	 * @return the last scissor element on the stack, or null of no scissor is set
	 */
	public Scissor last() {
		return stack.peekLast();
	}

	protected abstract void enableScissor();

	protected abstract void setScissor(Scissor scissor);

	protected abstract void disableScissor();

	/**
	 * @author DasBabyPixel
	 */
	public static class Scissor {

		@SuppressWarnings("javadoc")
		public final int x, y, w, h;

		/**
		 * @param x
		 * @param y
		 * @param w
		 * @param h
		 */
		public Scissor(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}

		@Override
		public String toString() {
			return "Scissor [x=" + x + ", y=" + y + ", w=" + w + ", h=" + h + "]";
		}

	}

}
