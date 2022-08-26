package gamelauncher.engine.render;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import de.dasbabypixel.api.property.NumberValue;
import it.unimi.dsi.fastutil.longs.LongArrayList;

/**
 * @author DasBabyPixel
 */
public class FrameCounter {

	private static final long second = TimeUnit.SECONDS.toNanos(1);

	private final NumberValue limit = NumberValue.zero();

	private final NumberValue frameNanos = NumberValue.zero();

	private final Buffer buffer = new Buffer();

	private final AtomicInteger lastFps = new AtomicInteger(0);

	private final AtomicInteger lastFrameCount = new AtomicInteger(0);

	private final Collection<Consumer<Integer>> updateListeners = ConcurrentHashMap.newKeySet();

	private final Collection<Consumer<Float>> avgUpdateListeners = ConcurrentHashMap.newKeySet();

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
		buffer.addFrame(nanos);
		int fps = buffer.frames1Second.size();
		if (lastFps.getAndSet(fps) != fps) {
			updateListeners.forEach(l -> l.accept(fps));
		}
		int average = buffer.frames5Second.size();
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
	 * @param nanoSleeper 
	 */
	public void frame(Consumer<Long> nanoSleeper) {
		float limit = this.limit();
		if (limit == 0) {
			offer(System.nanoTime());
		} else {
			boolean offer = false;
			long timeOffer = 0;
			if (!buffer.frames5Second.isEmpty()) {
				long frameNanos = this.frameNanos.longValue();
				long nextFrame = buffer.lastFrame.get() + frameNanos;
				if (nextFrame - System.nanoTime() < 0) {
					offer = true;
					timeOffer = System.nanoTime();
				} else {
					offer = true;
					nanoSleeper.accept(nextFrame - System.nanoTime());
					timeOffer = nextFrame;
				}
//				if (nextFrame > System.nanoTime() + frameNanos) {
//					sleepUntil(System.nanoTime() + frameNanos);
//					offer = true;
//					timeOffer = System.nanoTime() + frameNanos;
//				} else if (System.nanoTime() - nextFrame < 0) {
//					sleepUntil(nextFrame);
//					offer = true;
//					timeOffer = nextFrame;
//				}
			}
			offer(offer ? timeOffer : System.nanoTime());
		}
	}

	/**
	 * Stops sleeping if sleeping
	 * @param unparker 
	 */
	public void stopWaiting(Runnable unparker) {
		unparker.run();
	}

	/**
	 * @return the current fps
	 */
	public int getFps() {
		return buffer.frames1Second.size();
	}

	/**
	 * @return the average fps over the last five seconds
	 */
	public float getFpsAvg() {
		return buffer.frames5Second.size() / 5F;
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

	private class Buffer {

		private final LongArrayList frames1Second = new LongArrayList();

		private final LongArrayList frames5Second = new LongArrayList();

		private final AtomicLong lastFrame = new AtomicLong();

		public void addFrame(long frame) {
			removeOldFrames();
			frames1Second.add(frame);
			frames5Second.add(frame);
			lastFrame.set(frame);
		}

		private void removeOldFrames() {
			long compareTo = System.nanoTime() - second * 5;
			while (!frames5Second.isEmpty()) {
				long first = frames5Second.getLong(0);
				if (compareTo - first > 0) {
					frames5Second.removeLong(0);
					continue;
				}
				break;
			}
			compareTo = System.nanoTime() - second;
			while (!frames1Second.isEmpty()) {
				long first = frames1Second.getLong(0);
				if (compareTo - first > 0) {
					frames1Second.removeLong(0);
					continue;
				}
				break;
			}
		}

	}

}
