package gamelauncher.engine.render;

import de.dasbabypixel.api.property.NumberValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

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
	public Collection<Consumer<Integer>> updateListeners() {
		return this.updateListeners;
	}

	/**
	 * @param fpsConsumer
	 */
	public void addUpdateListener(Consumer<Integer> fpsConsumer) {
		this.updateListeners.add(fpsConsumer);
	}

	/**
	 * @param avgFpsConsumer
	 */
	public void addAvgUpdateListener(Consumer<Float> avgFpsConsumer) {
		this.avgUpdateListeners.add(avgFpsConsumer);
	}

	/**
	 * @return the average update listeners
	 */
	public Collection<Consumer<Float>> avgUpdateListeners() {
		return this.avgUpdateListeners;
	}

	private void offer(long nanos) {
		this.buffer.addFrame(nanos);
		int fps = this.buffer.frames1Second.size();
		if (this.lastFps.getAndSet(fps) != fps) {
			this.updateListeners.forEach(l -> l.accept(fps));
		}
		int average = this.buffer.frames5Second.size();
		if (this.lastFrameCount.getAndSet(average) != average) {
			this.avgUpdateListeners.forEach(l -> l.accept(average / 5.0F));
		}
	}

	/**
	 *
	 */
	public void frameNoWait() {
		this.offer(System.nanoTime());
	}

	/**
	 * @param nanoSleeper the nanosleeper function
	 */
	public void frame(Consumer<Long> nanoSleeper) {
		float limit = this.limit();
		if (limit == 0) {
			this.offer(System.nanoTime());
		} else {
			if (!this.buffer.frames5Second.isEmpty()) {
				long frameNanos = this.frameNanos.longValue();
				long nextFrame = this.buffer.lastFrame.get() + frameNanos;
				if (nextFrame - System.nanoTime() > 0) {
					nanoSleeper.accept(nextFrame - System.nanoTime());
				}
			}
			this.offer(System.nanoTime());
		}
	}

	/**
	 * @return the current fps
	 */
	public int fps() {
		return this.buffer.frames1Second.size();
	}

	/**
	 * @return the average fps over the last five seconds
	 */
	public float fpsAvg() {
		return this.buffer.frames5Second.size() / 5F;
	}

	/**
	 * @return the frameLimit
	 */
	public float limit() {
		return this.limit.floatValue();
	}

	/**
	 * Sets the frameLimit
	 *
	 * @param limit the new limit
	 */
	public void limit(float limit) {
		if (limit <= 0) {
			this.limit.setNumber(0);
			this.frameNanos.setNumber(0);
		} else {
			this.limit.setNumber(limit);
			this.frameNanos.setNumber(FrameCounter.second / limit);
		}
	}

	private static class Buffer {

		private final List<LongHandle> unusedHandles =
				Collections.synchronizedList(new ArrayList<>());

		private final List<LongHandle> frames1Second =
				Collections.synchronizedList(new ArrayList<>());

		private final List<LongHandle> frames5Second =
				Collections.synchronizedList(new ArrayList<>());

		private final AtomicLong lastFrame = new AtomicLong();

		public void addFrame(long frame) {
			this.removeOldFrames();
			LongHandle handle = this.getHandle(frame);
			this.frames1Second.add(handle);
			this.frames5Second.add(handle);
			this.lastFrame.set(frame);
		}

		private LongHandle getHandle(long value) {
			synchronized (this.unusedHandles) {
				if (!this.unusedHandles.isEmpty()) {
					LongHandle handle = this.unusedHandles.remove(0);
					handle.value = value;
					return handle;
				}
			}
			LongHandle handle = new LongHandle();
			handle.value = value;
			return handle;
		}

		private void removeOldFrames() {
			long compareTo = System.nanoTime() - FrameCounter.second * 5;
			synchronized (this.frames5Second) {
				while (!this.frames5Second.isEmpty()) {
					LongHandle first = this.frames5Second.get(0);
					if (compareTo - first.value > 0) {
						this.frames5Second.remove(0);
						this.unusedHandles.add(first);
						continue;
					}
					break;
				}
			}
			compareTo = System.nanoTime() - FrameCounter.second;
			while (!this.frames1Second.isEmpty()) {
				LongHandle first = this.frames1Second.get(0);
				if (compareTo - first.value > 0) {
					this.frames1Second.remove(0);
					continue;
				}
				break;
			}
		}

		private static class LongHandle {

			private long value;

		}

	}

}
