package gamelauncher.lwjgl.input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import gamelauncher.engine.input.Input;
import gamelauncher.lwjgl.render.LWJGLWindow;

public class LWJGLInput implements Input {

	private final LWJGLWindow window;
	private final LWJGLMouse mouse;
	private final List<Entry> pressed = Collections.synchronizedList(new ArrayList<>());
	private final Queue<QueueEntry> queue = new ConcurrentLinkedQueue<>();
	private final Collection<Listener> listeners = ConcurrentHashMap.newKeySet();

	private volatile QueueEntry fqentry = new QueueEntry(null, null);
	private volatile QueueEntry lqentry = fqentry;
	public final AtomicInteger qentrysize = new AtomicInteger();
	private volatile Entry fentry = new Entry(0, null);
	private volatile Entry lentry = fentry;
	public final AtomicInteger entrysize = new AtomicInteger();

	private final KeyboardEventListener keyboardEventListener = new KeyboardEventListener();
	private final MouseEventListener mouseEventListener = new MouseEventListener();

	public LWJGLInput(LWJGLWindow window) {
		this.window = window;
		this.mouse = this.window.mouse;
	}

	@Override
	public void handleInput() {
		QueueEntry qe;
		while ((qe = queue.poll()) != null) {
			event(qe.entry, qe.type);
			switch (qe.type) {
			case PRESSED:
				pressed.add(qe.entry);
				break;
			case RELEASED:
				int index = pressed.indexOf(qe.entry);
				if (index == -1) {
					freeEntry(qe.entry);
					break;
				}
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
			event(entry, InputType.HELD);
		}
	}

	private void event(Entry entry, InputType input) {
		switch (entry.type) {
		case KEYBOARD:
			keyEvent(input, entry.key);
			break;
		case MOUSE:
			mouseEvent(input, entry.key, entry.mx, entry.my);
			break;
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

	private void event(Consumer<Listener> consumer) {
		for (Listener l : listeners) {
			consumer.accept(l);
		}
	}

	private void mouseEvent(InputType inputType, int mouseButton, double mx, double my) {
		mouseEventListener.input = inputType;
		mouseEventListener.mouseButton = mouseButton;
		mouseEventListener.mx = mx;
		mouseEventListener.my = my;
		event(mouseEventListener);
	}

	private void keyEvent(InputType inputType, int key) {
		keyboardEventListener.input = inputType;
		keyboardEventListener.key = key;
		event(keyboardEventListener);
	}

	public void scroll(double xoffset, double yoffset) {
		mouseEvent(InputType.SCROLL, 0, xoffset, yoffset);
	}

	public void mouseMove(double mx, double my) {
		queue.add(newQueueEntry(newEntry(-1, mx, my), InputType.MOVE));
	}

	public void mousePress(int key, double mx, double my) {
		queue.add(newQueueEntry(newEntry(key, mx, my), InputType.PRESSED));
	}

	public void mouseRelease(int key, double mx, double my) {
		queue.add(newQueueEntry(newEntry(key, mx, my), InputType.RELEASED));
	}

	public void keyRepeat(int key) {
		queue.add(newQueueEntry(newEntry(key, DeviceType.KEYBOARD), InputType.REPEAT));
	}

	public void keyPress(int key) {
		queue.add(newQueueEntry(newEntry(key, DeviceType.KEYBOARD), InputType.PRESSED));
	}

	public void keyRelease(int key) {
		queue.add(newQueueEntry(newEntry(key, DeviceType.KEYBOARD), InputType.RELEASED));
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

	private Entry newEntry(int key, double mx, double my) {
		Entry e = null;
		if (fentry != lentry) {
			e = fentry;
			fentry = fentry.next;
			e.next = e;
			entrysize.decrementAndGet();
		}
		if (e == null) {
			e = new Entry(key, DeviceType.MOUSE, mx, my);
		} else {
			e.key = key;
			e.type = DeviceType.MOUSE;
			e.mx = mx;
			e.my = my;
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
		void handleKeyboard(InputType inputType, int key);

		void handleMouse(InputType inputType, int mouseButton, double mouseX, double mouseY);
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
		private double mx, my;

		private Entry next = this;

		public Entry(int key, DeviceType type) {
			this.key = key;
			this.type = type;
		}

		public Entry(int key, DeviceType type, double mx, double my) {
			this.key = key;
			this.type = type;
			this.mx = mx;
			this.my = my;
		}

		@Override
		public String toString() {
			return "Entry [key=" + key + ", type=" + type + ", mx=" + mx + ", my=" + my + "]";
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

	private static class MouseEventListener implements Consumer<Listener> {

		private InputType input;
		private int mouseButton;
		private double mx, my;

		@Override
		public void accept(Listener t) {
			t.handleMouse(input, mouseButton, mx, my);
		}
	}

	private static class KeyboardEventListener implements Consumer<Listener> {

		private InputType input;
		private int key;

		@Override
		public void accept(Listener t) {
			t.handleKeyboard(input, key);
		}
	}

	public static enum InputType {
		PRESSED, HELD, RELEASED, REPEAT, MOVE, SCROLL
	}

	public static enum DeviceType {
		MOUSE, KEYBOARD
	}
}
