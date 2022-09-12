package gamelauncher.lwjgl.input;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import gamelauncher.engine.input.Input;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.engine.util.keybind.KeybindManager;
import gamelauncher.engine.util.keybind.KeyboardKeybindEntry;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEntry;
import gamelauncher.lwjgl.render.glfw.GLFWWindow;
import gamelauncher.lwjgl.util.keybind.AllKeybind;
import gamelauncher.lwjgl.util.keybind.LWJGLKeybindManager;
import gamelauncher.lwjgl.util.keybind.LWJGLKeyboardKeybindEntry;
import gamelauncher.lwjgl.util.keybind.LWJGLMouseButtonKeybindEntry;
import gamelauncher.lwjgl.util.keybind.LWJGLMouseMoveKeybindEntry;
import gamelauncher.lwjgl.util.keybind.LWJGLScrollKeybindEntry;

/**
 * @author DasBabyPixel
 *
 */
public class LWJGLInput implements Input {

	private final List<Entry> pressed = Collections.synchronizedList(new ArrayList<>());

	private final Queue<QueueEntry> queue = new ConcurrentLinkedQueue<>();

	private final List<Entry> mousePressed = new ArrayList<>();

	private volatile QueueEntry fqentry = new QueueEntry(null, null);

	private volatile QueueEntry lqentry = fqentry;

	private final AtomicInteger qentrysize = new AtomicInteger();

	private volatile Entry fentry = new Entry(0, (char) 0, null);

	private volatile Entry lentry = fentry;

	private final AtomicInteger entrysize = new AtomicInteger();

	private final KeybindManager keybindManager;

	/**
	 * @param window
	 */
	public LWJGLInput(GLFWWindow window) {
		this.keybindManager = window.getLauncher().getKeybindManager();
	}

	@Override
	public void handleInput() throws GameException {
		QueueEntry qe;
		while ((qe = queue.poll()) != null) {
			event(qe.entry, qe.type);
			switch (qe.type) {
			case PRESSED:
				pressed.add(qe.entry);
				if (qe.entry.type == DeviceType.MOUSE) {
					mousePressed.add(qe.entry);
				}
				break;
			case RELEASED:
				int index = pressed.indexOf(qe.entry);
				if (index == -1) {
					freeEntry(qe.entry);
					break;
				}
				Entry inPressed = pressed.remove(index);
				if (inPressed.type == DeviceType.MOUSE) {
					mousePressed.remove(inPressed);
				}
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

	private void event(Entry entry, InputType input) throws GameException {
		switch (entry.type) {
		case KEYBOARD:
			keyEvent(input, entry.key, entry.scancode);
			break;
		case MOUSE:
			mouseEvent(input, entry.key, entry.omx, entry.omy, entry.mx, entry.my);
			break;
		}
	}

	private void freeEntry(Entry entry) {
		entry.next = entry;
		lentry.next = entry;
		lentry = entry;
		entrysize.incrementAndGet();
	}

	private void mouseEvent(InputType inputType, int mouseButton, float omx, float omy, float mx, float my)
			throws GameException {
		if (inputType == InputType.SCROLL) {
			keybindManager.post(keybind -> {
				int id = LWJGLKeybindManager.SCROLL;
				if (keybind.getUniqueId() != id) {
					return null;
				}
				return new LWJGLScrollKeybindEntry(keybind, mx, my);
			});
		} else {
			if (inputType == InputType.MOVE) {
				for (Entry e : mousePressed) {
					e.mx = mx;
					e.my = my;
				}
			}
			keybindManager.post(keybind -> {
				int id = LWJGLKeybindManager.MOUSE_ADD + mouseButton;
				if (keybind instanceof AllKeybind) {
					((AllKeybind) keybind).id = id;
				}
				if (keybind.getUniqueId() != id) {
					return null;
				}
				KeybindEntry ke = null;
				switch (inputType) {
				case HELD:
					ke = new LWJGLMouseButtonKeybindEntry(keybind, mx, my, MouseButtonKeybindEntry.Type.HOLD);
					break;
				case MOVE:
					ke = new LWJGLMouseMoveKeybindEntry(keybind, omx, omy, mx, my);
					break;
				case PRESSED:
					ke = new LWJGLMouseButtonKeybindEntry(keybind, mx, my, MouseButtonKeybindEntry.Type.PRESS);
					break;
				case RELEASED:
					ke = new LWJGLMouseButtonKeybindEntry(keybind, mx, my, MouseButtonKeybindEntry.Type.RELEASE);
					break;
				default:
					throw new UnsupportedOperationException();
				}
				return ke;
			});
		}
	}

	private void keyEvent(InputType inputType, int key, int scancode) throws GameException {
		keybindManager.post(keybind -> {
			int id = key == GLFW_KEY_UNKNOWN ? LWJGLKeybindManager.KEYBOARD_SCANCODE_ADD + scancode
					: LWJGLKeybindManager.KEYBOARD_ADD + key;
			if (keybind instanceof AllKeybind) {
				((AllKeybind) keybind).id = id;
			}
			if (keybind.getUniqueId() != id) {
				return null;
			}
			KeybindEntry ke = null;
			switch (inputType) {
			case HELD:
				ke = new LWJGLKeyboardKeybindEntry(keybind, KeyboardKeybindEntry.Type.HOLD);
				break;
			case PRESSED:
				ke = new LWJGLKeyboardKeybindEntry(keybind, KeyboardKeybindEntry.Type.PRESS);
				break;
			case RELEASED:
				ke = new LWJGLKeyboardKeybindEntry(keybind, KeyboardKeybindEntry.Type.RELEASE);
				break;
			case REPEAT:
				ke = new LWJGLKeyboardKeybindEntry(keybind, KeyboardKeybindEntry.Type.REPEAT);
				break;
			case CHARACTER:
				ke = new LWJGLKeyboardKeybindEntry(keybind, KeyboardKeybindEntry.Type.CHARACTER);
				break;
			default:
				throw new UnsupportedOperationException();
			}
			return ke;
		});
	}

	/**
	 * Queues a scroll event
	 * 
	 * @param xoffset
	 * @param yoffset
	 * @throws GameException
	 */
	public void scroll(float xoffset, float yoffset) throws GameException {
		queue.add(newQueueEntry(newEntry(0, 0, 0, xoffset, yoffset), InputType.SCROLL));
	}

	/**
	 * Queues a mouse move event
	 * 
	 * @param omx
	 * @param omy
	 * @param mx
	 * @param my
	 */
	public void mouseMove(float omx, float omy, float mx, float my) {
		queue.add(newQueueEntry(newEntry(-1, omx, omy, mx, my), InputType.MOVE));
	}

	/**
	 * Queues a mouse press event
	 * 
	 * @param key
	 * @param mx
	 * @param my
	 */
	public void mousePress(int key, float mx, float my) {
		queue.add(newQueueEntry(newEntry(key, 0, 0, mx, my), InputType.PRESSED));
	}

	/**
	 * Queues a mouse release event
	 * 
	 * @param key
	 * @param mx
	 * @param my
	 */
	public void mouseRelease(int key, float mx, float my) {
		queue.add(newQueueEntry(newEntry(key, 0, 0, mx, my), InputType.RELEASED));
	}

	/**
	 * Queues a key repeat event
	 * 
	 * @param key
	 * @param scancode
	 * @param ch
	 */
	public void keyRepeat(int key, int scancode, char ch) {
		queue.add(newQueueEntry(newEntry(key, scancode, ch, DeviceType.KEYBOARD), InputType.REPEAT));
	}

	/**
	 * Queues a key press event
	 * 
	 * @param key
	 * @param scancode
	 * @param ch
	 */
	public void keyPress(int key, int scancode, char ch) {
		queue.add(newQueueEntry(newEntry(key, scancode, ch, DeviceType.KEYBOARD), InputType.PRESSED));
	}

	/**
	 * Queues a key release event
	 * 
	 * @param key
	 * @param scancode
	 * @param ch
	 */
	public void keyRelease(int key, int scancode, char ch) {
		queue.add(newQueueEntry(newEntry(key, scancode, ch, DeviceType.KEYBOARD), InputType.RELEASED));
	}

	/**
	 * Queues a character event
	 * 
	 * @param ch
	 */
	public void character(char ch) {
		queue.add(newQueueEntry(newEntry(0, 0, ch, DeviceType.KEYBOARD), InputType.CHARACTER));
	}

	private Entry newEntry(int key, int scancode, char ch, DeviceType deviceType) {
		Entry e = null;
		if (fentry != lentry) {
			e = fentry;
			fentry = fentry.next;
			e.next = e;
			entrysize.decrementAndGet();
		}
		if (e == null) {
			e = new Entry(key, ch, deviceType);
		} else {
			e.key = key;
			e.type = deviceType;
		}
		return e;
	}

	private Entry newEntry(int key, float omx, float omy, float mx, float my) {
		Entry e = null;
		if (fentry != lentry) {
			e = fentry;
			fentry = fentry.next;
			e.next = e;
			entrysize.decrementAndGet();
		}
		if (e == null) {
			e = new Entry(key, DeviceType.MOUSE, omx, omy, mx, my);
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

		private int scancode;

		private DeviceType type;

		private float mx, my;

		private float omx;

		private float omy;

		private char ch;

		private Entry next = this;

		public Entry(int key, char ch, DeviceType type) {
			this.key = key;
			this.ch = ch;
			this.type = type;
		}

		public Entry(int key, DeviceType type, float omx, float omy, float mx, float my) {
			this.key = key;
			this.type = type;
			this.mx = mx;
			this.my = my;
			this.omx = omx;
			this.omy = omy;
		}

		@Override
		public String toString() {
			return "Entry [key=" + key + ", type=" + type + ", mx=" + mx + ", my=" + my + "]";
		}

		@Override
		public int hashCode() {
			return Objects.hash(key, ch, scancode, type);
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
			return key == other.key && type == other.type && scancode == other.scancode && ch == other.ch;
		}

	}

	/**
	 * @author DasBabyPixel
	 *
	 */
	public static enum InputType {
		/**
		 * When a mouse button or key is pressed
		 */
		PRESSED,
		/**
		 * When a mouse button or key is being held
		 */
		HELD,
		/**
		 * When a mouse button or key is released
		 */
		RELEASED,
		/**
		 * When a key is being held for text input
		 */
		REPEAT,
		/**
		 * When the mouse is being moved
		 */
		MOVE,
		/**
		 * When scrolling
		 */
		SCROLL,
		/**
		 * When a character is pressed. For text input
		 */
		CHARACTER
	}

	/**
	 * @author DasBabyPixel
	 */
	public static enum DeviceType {
		/**
		 * The Mouse
		 */
		MOUSE,
		/**
		 * The Keyboard
		 */
		KEYBOARD
	}

}
