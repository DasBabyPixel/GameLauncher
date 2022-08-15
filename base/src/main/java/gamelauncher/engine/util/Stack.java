package gamelauncher.engine.util;

import java.util.concurrent.atomic.AtomicReference;

/**
 * LIFO Stack Thread-Safe
 * 
 * @author DasBabyPixel
 * @param <T>
 */
public class Stack<T> {

	private final AtomicReference<Entry<T>> aentry = new AtomicReference<>();

	private volatile int size = 0;

	/**
	 * @return the size
	 */
	public int size() {
		return size;
	}

	/**
	 * @return the first value
	 */
	public T pop() {
		while (true) {
			Entry<T> e = aentry.get();
			if (e == null) {
				return null;
			}
			if (!aentry.compareAndSet(e, e.prev)) {
				continue;
			}
			size--;
			return e.value;
		}
	}

	/**
	 * @return the first value
	 */
	public T peek() {
		Entry<T> e = aentry.get();
		return e == null ? null : e.value;
	}

	/**
	 * @param value
	 */
	public void push(T value) {
		while (true) {
			Entry<T> e = new Entry<T>(aentry.get(), value);
			if (aentry.compareAndSet(e.prev, e)) {
				break;
			}
		}
		size++;
	}

	private static class Entry<T> {

		private final Entry<T> prev;

		private final T value;

		public Entry(Entry<T> prev, T value) {
			this.prev = prev;
			this.value = value;
		}

	}

}
