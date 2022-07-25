package gamelauncher.engine.render;

import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

import de.dasbabypixel.api.property.NumberValue;

/**
 * @author DasBabyPixel
 */
public class FrameCounter {

	private static final long second = TimeUnit.SECONDS.toNanos(1);

	private final Deque<Long> frames = new ConcurrentLinkedDeque<>();
	private final Deque<Long> secondframes = new ConcurrentLinkedDeque<>();
	private final NumberValue limit = NumberValue.zero();
	private final NumberValue frameNanos = NumberValue.zero();
	private final AtomicReference<Thread> sleeping = new AtomicReference<>(null);
	private final AtomicInteger lastFps = new AtomicInteger(0);
	private final AtomicInteger lastFrameCount = new AtomicInteger(0);
	private final Collection<Consumer<Integer>> updateListeners = ConcurrentHashMap.newKeySet();
	private final Collection<Consumer<Float>> avgUpdateListeners = ConcurrentHashMap.newKeySet();

	private void removeOldFrames() {
		long compareTo = System.nanoTime() - second * 5;
		while (!frames.isEmpty()) {
			long first = frames.peekFirst();
			if (compareTo - first > 0) {
				frames.pollFirst();
				continue;
			}
			break;
		}
		compareTo = System.nanoTime() - second;
		while (!secondframes.isEmpty()) {
			long first = secondframes.peekFirst();
			if (compareTo - first > 0) {
				secondframes.pollFirst();
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

	/**
	 * @param avgFpsConsumer
	 */
	public void addAvgUpdateListener(Consumer<Float> avgFpsConsumer) {
		avgUpdateListeners.add(avgFpsConsumer);
	}

	/**
	 * @return the average update listeners
	 */
	public Collection<Consumer<Float>> getAvgUpdateListeners() {
		return avgUpdateListeners;
	}

	private void offer(long nanos) {
		removeOldFrames();
		frames.offer(nanos);
		secondframes.offer(nanos);
		int fps = secondframes.size();
		if (lastFps.getAndSet(fps) != fps) {
			updateListeners.forEach(l -> l.accept(fps));
		}
		int average = frames.size();
		if (lastFrameCount.getAndSet(average) != average) {
			avgUpdateListeners.forEach(l -> l.accept(average / 5.0F));
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
		float limit = this.limit();
		if (limit == 0) {
			offer(System.nanoTime());
		} else {
			boolean offer = false;
			long timeOffer = 0;
			Long lastFrameL = frames.peekLast();
			if (lastFrameL != null) {
				long frameNanos = this.frameNanos.longValue();
				long nextFrame = lastFrameL.longValue() + frameNanos;
				if (nextFrame > System.nanoTime() + frameNanos) {
					sleepUntil(System.nanoTime() + frameNanos);
					offer = true;
					timeOffer = System.nanoTime() + frameNanos;
				} else if (System.nanoTime() - nextFrame < 0) {
					sleepUntil(nextFrame);
					offer = true;
					timeOffer = nextFrame;
				}
			}
			offer(offer ? timeOffer : System.nanoTime());
		}
	}

	/**
	 * Stops sleeping if sleeping
	 */
	public void stopWaiting() {
		Thread sleep = sleeping.getAndSet(null);
		if (sleep != null) {
			LockSupport.unpark(sleep);
		}
	}

	private void sleepUntil(long nanos) {
		Thread cur = Thread.currentThread();
		if (sleeping.compareAndSet(null, cur)) {
			boolean millis = true;
			while (System.nanoTime() - nanos < 0) {
				if (millis) {
					long mls = TimeUnit.NANOSECONDS.toMillis(nanos - System.nanoTime());
					if (mls > 1) {
//					try {
						if (sleeping.get() == null) {
							return;
						}
						LockSupport.parkUntil(System.currentTimeMillis() + mls);
//						Thread.sleep(mls - 1);
//					} catch (InterruptedException ex) {
//						ex.printStackTrace();
//					}
					}
					millis = false;
				}
				Thread.yield();
				if (sleeping.get() == null) {
					return;
				}
			}
			sleeping.compareAndSet(cur, null);
		} else {
			throw new IllegalStateException("A thread is already sleeping");
		}
	}

	/**
	 * @return the current fps
	 */
	public int getFps() {
		removeOldFrames();
		if (secondframes.size() >= 2) {
			Long first = secondframes.peekFirst();
			Long last = secondframes.peekLast();
			if (first == null || last == null) {
				return secondframes.size();
			}
			double diff = last.longValue() - first.longValue();
			double mult = second / diff;
			return (int) (mult * secondframes.size());
		}
		return secondframes.size();
	}

	/**
	 * @return the average fps over the last five seconds
	 */
	public float getFpsAvg() {
		removeOldFrames();
		return frames.size() / 5F;
	}

	/**
	 * @return the frameLimit
	 */
	public float limit() {
		return limit.floatValue();
	}

	/**
	 * Sets the frameLimit
	 * 
	 * @param limit
	 */
	public void limit(float limit) {
		if (limit <= 0) {
			this.limit.setNumber(0);
			this.frameNanos.setNumber(0);
		} else {
			this.limit.setNumber(limit);
			this.frameNanos.setNumber(second / limit);
		}
	}
}
