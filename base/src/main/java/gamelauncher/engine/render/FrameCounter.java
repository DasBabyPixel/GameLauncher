package gamelauncher.engine.render;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class FrameCounter {

	private static final long second = TimeUnit.SECONDS.toNanos(1);

	private final Deque<Long> frames = new ConcurrentLinkedDeque<>();
	private final AtomicLong limit = new AtomicLong(0);
	private final AtomicLong frameNanos = new AtomicLong(0);

	private void removeOldFrames() {
		long compareTo = System.nanoTime() - second;
		while (!frames.isEmpty()) {
			long first = frames.peekFirst();
			if (compareTo - first > 0) {
				frames.pollFirst();
				continue;
			}
			break;
		}
	}

	public void frameNoWait() {
		removeOldFrames();
		frames.offer(System.nanoTime());
	}

	public void frame() {
		long limit = this.limit();
		if (limit == 0) {
			removeOldFrames();
			frames.offer(System.nanoTime());
		} else {
			boolean offer = false;
			long timeOffer = 0;
			Long lastFrameL = frames.peekLast();
			if (lastFrameL != null) {
				long frameNanos = this.frameNanos.get();
				long nextFrame = lastFrameL.longValue() + frameNanos;
				if (System.nanoTime() - nextFrame < 0) {
					sleepUntil(nextFrame);
					offer = true;
					timeOffer = nextFrame;
				}
			}
			removeOldFrames();
			frames.offer(offer ? timeOffer : System.nanoTime());
		}
	}

	private void sleepUntil(long nanos) {
		while (System.nanoTime() - nanos < 0) {
			Thread.yield();
		}
	}

	public int getFps() {
		removeOldFrames();
		if (frames.size() >= 2) {
			Long first = frames.peekFirst();
			Long last = frames.peekLast();
			if (first == null || last == null) {
				return frames.size();
			}
			double diff = last.longValue() - first.longValue();
			double mult = second / diff;
			return (int) (mult * frames.size());
		}
		return frames.size();
	}

	public long limit() {
		return limit.get();
	}

	public void limit(long limit) {
		if (limit <= 0) {
			this.limit.set(0);
			this.frameNanos.set(0);
		} else {
			this.limit.set(limit);
			this.frameNanos.set(second / limit);
		}
	}
}
