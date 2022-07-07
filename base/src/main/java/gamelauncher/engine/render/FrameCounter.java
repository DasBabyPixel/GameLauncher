package gamelauncher.engine.render;

import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * @author DasBabyPixel
 */
public class FrameCounter {

	private static final long second = TimeUnit.SECONDS.toNanos(1);

	private final Deque<Long> frames = new ConcurrentLinkedDeque<>();
	private final AtomicLong limit = new AtomicLong(0);
	private final AtomicLong frameNanos = new AtomicLong(0);
	private final AtomicInteger lastFps = new AtomicInteger(0);
	private final Collection<Consumer<Integer>> updateListeners = ConcurrentHashMap.newKeySet();

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

	/**
	 * @return the update listeners
	 */
	public Collection<Consumer<Integer>> getUpdateListeners() {
		return updateListeners;
	}

	/**
	 * @param fpsConsumer
	 */
	public void addUpdateListener(Consumer<Integer> fpsConsumer) {
		updateListeners.add(fpsConsumer);
	}

	private void offer(long nanos) {
		removeOldFrames();
		frames.offer(nanos);
		int fps = frames.size();
		if (lastFps.getAndSet(fps) != fps) {
			updateListeners.forEach(l -> l.accept(fps));
		}
	}

	/**
	 */
	public void frameNoWait() {
		offer(System.nanoTime());
	}

	/**
	 */
	public void frame() {
		long limit = this.limit();
		if (limit == 0) {
			offer(System.nanoTime());
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
			offer(offer ? timeOffer : System.nanoTime());
		}
	}

	private void sleepUntil(long nanos) {
		boolean millis = true;
		while (System.nanoTime() - nanos < 0) {
			if (millis) {
				long mls = TimeUnit.NANOSECONDS.toMillis(nanos - System.nanoTime());
				if (mls > 1) {
					try {
						Thread.sleep(mls - 1);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
				millis = false;
			}
			Thread.yield();
		}
	}

	/**
	 * @return the current fps
	 */
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

	/**
	 * @return the frameLimit
	 */
	public long limit() {
		return limit.get();
	}

	/**
	 * Sets the frameLimit
	 * 
	 * @param limit
	 */
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
