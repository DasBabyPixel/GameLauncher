package gamelauncher.lwjgl.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import gamelauncher.engine.input.Input;

public class LWJGLInput implements Input {

	private final LWJGLWindow window;
	private final List<Entry> pressed = Collections.synchronizedList(new ArrayList<>());
	private final Queue<QueueEntry> queue = new ConcurrentLinkedQueue<>();
	private final Collection<Listener> listeners = ConcurrentHashMap.newKeySet();

	private volatile QueueEntry fqentry = new QueueEntry(null, null);
	private volatile QueueEntry lqentry = fqentry;
	public final AtomicInteger qentrysize = new AtomicInteger();
	private volatile Entry fentry = new Entry(0, null);
	private volatile Entry lentry = fentry;
	public final AtomicInteger entrysize = new AtomicInteger();

	public LWJGLInput(LWJGLWindow window) {
		this.window = window;
	}

	@Override
	public void handleInput() {
		QueueEntry qe;
		while ((qe = queue.poll()) != null) {
			event(qe.entry.type, qe.type, qe.entry.key);
			switch (qe.type) {
			case PRESSED:
				pressed.add(qe.entry);
				break;
			case RELEASED:
				int index = pressed.indexOf(qe.entry);
				Entry inPressed = pressed.remove(index);
				freeEntry(inPressed);
			default:
				freeEntry(qe.entry);
				break;
			}
			qe.next = qe;
			lqentry.next = qe;
			lqentry = lqentry.next;
			qentrysize.incrementAndGet();
		}
		for (Entry entry : pressed) {
			event(entry.type, InputType.HELD, entry.key);
		}
	}

	private void freeEntry(Entry entry) {
		entry.next = entry;
		lentry.next = entry;
		lentry = entry;
		entrysize.incrementAndGet();
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	private void event(DeviceType deviceType, InputType inputType, int key) {
		for (Listener listener : listeners) {
			listener.handle(inputType, deviceType, key);
		}
	}

	public void repeat(int key, DeviceType type) {
		queue.add(newQueueEntry(newEntry(key, type), InputType.REPEAT));
	}

	public void press(int key, DeviceType type) {
		queue.add(newQueueEntry(newEntry(key, type), InputType.PRESSED));
	}

	public void release(int key, DeviceType type) {
		queue.add(newQueueEntry(newEntry(key, type), InputType.RELEASED));
	}

	private Entry newEntry(int key, DeviceType deviceType) {
		Entry e = null;
		if (fentry != lentry) {
			e = fentry;
			fentry = fentry.next;
			e.next = e;
			entrysize.decrementAndGet();
		}
		if (e == null) {
			e = new Entry(key, deviceType);
		} else {
			e.key = key;
			e.type = deviceType;
		}
		return e;
	}

	private QueueEntry newQueueEntry(Entry entry, InputType inputType) {
		QueueEntry e = null;
		if (fqentry != lqentry) {
			e = fqentry;
			fqentry = fqentry.next;
			e.next = e;
			qentrysize.decrementAndGet();
		}
		if (e == null) {
			e = new QueueEntry(entry, inputType);
		} else {
			e.entry = entry;
			e.type = inputType;
		}
		return e;
	}

	@Override
	public boolean hasKeyboard() {
		return true;
	}

	public static interface Listener {
		void handle(InputType inputType, DeviceType deviceType, int key);
	}

	private static class QueueEntry {

		private Entry entry;
		private InputType type;

		private QueueEntry next = this;

		public QueueEntry(Entry entry, InputType type) {
			this.entry = entry;
			this.type = type;
		}

		@Override
		public int hashCode() {
			return Objects.hash(entry, type);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			QueueEntry other = (QueueEntry) obj;
			return Objects.equals(entry, other.entry) && type == other.type;
		}
	}

	private static class Entry {

		private int key;
		private DeviceType type;

		private Entry next = this;

		public Entry(int key, DeviceType type) {
			this.key = key;
			this.type = type;
		}

		@Override
		public int hashCode() {
			return Objects.hash(key, type);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Entry other = (Entry) obj;
			return key == other.key && type == other.type;
		}
	}

	public static enum InputType {
		PRESSED, HELD, RELEASED, REPEAT
	}

	public static enum DeviceType {
		MOUSE, KEYBOARD
	}
}
