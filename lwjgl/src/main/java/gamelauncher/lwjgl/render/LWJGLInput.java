package gamelauncher.lwjgl.render;

import java.util.Collection;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import gamelauncher.engine.input.Input;

public class LWJGLInput implements Input {

	private final LWJGLWindow window;
	private final Collection<Entry> pressed = ConcurrentHashMap.newKeySet();
	private final Queue<QueueEntry> queue = new ConcurrentLinkedQueue<>();
	private final Collection<Listener> listeners = ConcurrentHashMap.newKeySet();

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
				pressed.remove(qe.entry);
				break;
			default:
				break;
			}
		}
		for (Entry entry : pressed) {
			event(entry.type, InputType.HELD, entry.key);
		}
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
		queue.add(new QueueEntry(new Entry(key, type), InputType.REPEAT));
	}

	public void press(int key, DeviceType type) {
		queue.add(new QueueEntry(new Entry(key, type), InputType.PRESSED));
	}

	public void release(int key, DeviceType type) {
		queue.add(new QueueEntry(new Entry(key, type), InputType.RELEASED));
	}

	@Override
	public boolean hasKeyboard() {
		return true;
	}

	public static interface Listener {
		void handle(InputType inputType, DeviceType deviceType, int key);
	}

	public static class QueueEntry {
		private final Entry entry;
		private final InputType type;

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
		private final int key;
		private final DeviceType type;

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
