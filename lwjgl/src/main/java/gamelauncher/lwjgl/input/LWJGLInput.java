package gamelauncher.lwjgl.input;

import gamelauncher.engine.input.Input;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.KeybindManager;
import gamelauncher.engine.util.keybind.KeyboardKeybindEntry;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEntry;
import gamelauncher.lwjgl.render.glfw.GLFWFrame;
import gamelauncher.lwjgl.util.keybind.*;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author DasBabyPixel
 */
public class LWJGLInput implements Input {

	private final List<Entry> pressed = Collections.synchronizedList(new ArrayList<>());
	private final Queue<QueueEntry> queue = new ConcurrentLinkedQueue<>();
	private final List<Entry> mousePressed = new ArrayList<>();
	private final KeybindManager keybindManager;

	public LWJGLInput(GLFWFrame frame) {
		this.keybindManager = frame.launcher().keybindManager();
	}

	@Override
	public void handleInput() throws GameException {
		QueueEntry qe;
		while ((qe = this.queue.poll()) != null) {
			this.event(qe.entry, qe.type);
			switch (qe.type) {
				case PRESSED:
					this.pressed.add(qe.entry);
					if (qe.entry.type == DeviceType.MOUSE) {
						this.mousePressed.add(qe.entry);
					}
					break;
				case RELEASED:
					int index = this.pressed.indexOf(qe.entry);
					if (index == -1) {
						break;
					}
					Entry inPressed = this.pressed.remove(index);
					if (inPressed.type == DeviceType.MOUSE) {
						this.mousePressed.remove(inPressed);
					}
				default:
					break;
			}
		}
		for (Entry entry : this.pressed) {
			this.event(entry, InputType.HELD);
		}
	}

	private void event(Entry entry, InputType input) throws GameException {
		switch (entry.type) {
			case KEYBOARD -> this.keyEvent(input, entry.key, entry.scancode);
			case MOUSE ->
					this.mouseEvent(input, entry.key, entry.omx, entry.omy, entry.mx, entry.my);
		}
	}

	private void mouseEvent(InputType inputType, int mouseButton, float omx, float omy, float mx,
			float my) throws GameException {
		if (inputType == InputType.SCROLL) {
			this.keybindManager.post(keybind -> {
				int id = LWJGLKeybindManager.SCROLL;
				if (keybind instanceof AllKeybind) {
					((AllKeybind) keybind).id = id;
				}
				if (keybind.uniqueId() != id) {
					return null;
				}
				return new LWJGLScrollKeybindEntry(keybind, mx, my);
			});
		} else {
			if (inputType == InputType.MOVE) {
				for (Entry e : this.mousePressed) {
					e.mx = mx;
					e.my = my;
				}
			}
			this.keybindManager.post(keybind -> {
				int id = LWJGLKeybindManager.MOUSE_ADD + mouseButton;
				if (keybind instanceof AllKeybind) {
					((AllKeybind) keybind).id = id;
				}
				if (keybind.uniqueId() != id) {
					return null;
				}
				return switch (inputType) {
					case HELD -> new LWJGLMouseButtonKeybindEntry(keybind, mx, my,
							MouseButtonKeybindEntry.Type.HOLD);
					case MOVE -> new LWJGLMouseMoveKeybindEntry(keybind, omx, omy, mx, my);
					case PRESSED -> new LWJGLMouseButtonKeybindEntry(keybind, mx, my,
							MouseButtonKeybindEntry.Type.PRESS);
					case RELEASED -> new LWJGLMouseButtonKeybindEntry(keybind, mx, my,
							MouseButtonKeybindEntry.Type.RELEASE);
					default -> throw new UnsupportedOperationException();
				};
			});
		}
	}

	private void keyEvent(InputType inputType, int key, int scancode) throws GameException {
		this.keybindManager.post(keybind -> {
			int id = key == GLFW.GLFW_KEY_UNKNOWN ? LWJGLKeybindManager.KEYBOARD_SCANCODE_ADD
					+ scancode : LWJGLKeybindManager.KEYBOARD_ADD + key;
			if (keybind instanceof AllKeybind) {
				((AllKeybind) keybind).id = id;
			}
			if (keybind.uniqueId() != id) {
				return null;
			}
			return switch (inputType) {
				case HELD -> new LWJGLKeyboardKeybindEntry(keybind, KeyboardKeybindEntry.Type.HOLD);
				case PRESSED ->
						new LWJGLKeyboardKeybindEntry(keybind, KeyboardKeybindEntry.Type.PRESS);
				case RELEASED ->
						new LWJGLKeyboardKeybindEntry(keybind, KeyboardKeybindEntry.Type.RELEASE);
				case REPEAT ->
						new LWJGLKeyboardKeybindEntry(keybind, KeyboardKeybindEntry.Type.REPEAT);
				case CHARACTER ->
						new LWJGLKeyboardKeybindEntry(keybind, KeyboardKeybindEntry.Type.CHARACTER);
				default -> throw new UnsupportedOperationException();
			};
		});
	}

	public void scroll(float xoffset, float yoffset) throws GameException {
		this.queue.add(
				this.newQueueEntry(this.newEntry(0, 0, 0, xoffset, yoffset), InputType.SCROLL));
	}

	public void mouseMove(float omx, float omy, float mx, float my) {
		this.queue.add(this.newQueueEntry(this.newEntry(-1, omx, omy, mx, my), InputType.MOVE));
	}

	public void mousePress(int key, float mx, float my) {
		this.queue.add(this.newQueueEntry(this.newEntry(key, 0, 0, mx, my), InputType.PRESSED));
	}

	public void mouseRelease(int key, float mx, float my) {
		this.queue.add(this.newQueueEntry(this.newEntry(key, 0, 0, mx, my), InputType.RELEASED));
	}

	public void keyRepeat(int key, int scancode, char ch) {
		this.queue.add(this.newQueueEntry(this.newEntry(key, scancode, ch), InputType.REPEAT));
	}

	public void keyPress(int key, int scancode, char ch) {
		this.queue.add(this.newQueueEntry(this.newEntry(key, scancode, ch), InputType.PRESSED));
	}

	public void keyRelease(int key, int scancode, char ch) {
		this.queue.add(this.newQueueEntry(this.newEntry(key, scancode, ch), InputType.RELEASED));
	}

	public void character(char ch) {
		this.queue.add(this.newQueueEntry(this.newEntry(0, 0, ch), InputType.CHARACTER));
	}

	private Entry newEntry(int key, int scancode, char ch) {
		return new Entry(key, scancode, ch, DeviceType.KEYBOARD);
	}

	private Entry newEntry(int key, float omx, float omy, float mx, float my) {
		return new Entry(key, DeviceType.MOUSE, omx, omy, mx, my);
	}

	private QueueEntry newQueueEntry(Entry entry, InputType inputType) {
		return new QueueEntry(entry, inputType);
	}

	/**
	 * @author DasBabyPixel
	 */
	public enum InputType {
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
	public enum DeviceType {
		/**
		 * The Mouse
		 */
		MOUSE,
		/**
		 * The Keyboard
		 */
		KEYBOARD
	}


	private record QueueEntry(Entry entry, InputType type) {

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (this.getClass() != obj.getClass())
				return false;
			QueueEntry other = (QueueEntry) obj;
			return Objects.equals(this.entry, other.entry) && this.type == other.type;
		}

	}


	private static class Entry {

		private final int key;
		private final DeviceType type;
		private int scancode;
		private float mx, my;

		private float omx;

		private float omy;

		private char ch;

		public Entry(int key, int scancode, char ch, DeviceType type) {
			this.key = key;
			this.scancode = scancode;
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
		public int hashCode() {
			return Objects.hash(this.key, this.ch, this.scancode, this.type);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (this.getClass() != obj.getClass())
				return false;
			Entry other = (Entry) obj;
			return this.key == other.key && this.type == other.type
					&& this.scancode == other.scancode && this.ch == other.ch;
		}

		@Override
		public String toString() {
			return "Entry [key=" + this.key + ", type=" + this.type + ", mx=" + this.mx + ", my="
					+ this.my + "]";
		}

	}

}
