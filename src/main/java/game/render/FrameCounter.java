package game.render;

import java.util.concurrent.*;

public class FrameCounter {

	private static final long second = TimeUnit.SECONDS.toNanos(1);

	private long lastFrameNanos = Long.MIN_VALUE;
	private long nextFrameResetNanos = Long.MIN_VALUE;
	private int cfps;
	private int fps;

	public void frame() {
		if (nextFrameResetNanos == Long.MIN_VALUE) {
			lastFrameNanos = System.nanoTime();
			nextFrameResetNanos = lastFrameNanos + second;
			cfps++;
			return;
		}
		long cur = System.nanoTime();
		if (nextFrameResetNanos < cur) {
			nextFrameResetNanos += second;
			fps = cfps;
			cfps = 0;
			if (nextFrameResetNanos < cur) {
				nextFrameResetNanos = cur;
			}
		}
		lastFrameNanos = cur;
		cfps++;
	}

	public long getLastFrameNanos() {
		return lastFrameNanos;
	}

	public long getNextFrameResetNanos() {
		return nextFrameResetNanos;
	}

	public int getFps() {
		return fps;
	}
}
