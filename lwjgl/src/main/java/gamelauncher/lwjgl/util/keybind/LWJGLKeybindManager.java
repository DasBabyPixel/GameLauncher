package gamelauncher.lwjgl.util.keybind;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.event.events.util.keybind.KeybindEntryEvent;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameFunction;
import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.engine.util.keybind.KeybindManager;

/**
 * @author DasBabyPixel
 *
 */
public class LWJGLKeybindManager extends AbstractGameResource implements KeybindManager {

	private static final String MOUSEBUTTON_PREFIX = "Button ";

	/**
	 * The number added for Keyboard ids
	 */
	public static final int KEYBOARD_ADD = 0;
	/**
	 * The number for scroll ids
	 */
	public static final int SCROLL = 9999;
	/**
	 * The number added for mouse ids
	 */
	public static final int MOUSE_ADD = 10000;
	/**
	 * The number added for keyboard scancodes
	 */
	public static final int KEYBOARD_SCANCODE_ADD = 20000;

	private final GameLauncher launcher;
	private final Map<Integer, String> names = new ConcurrentHashMap<>();
	/**
	 * The Map of {@link Keybind}s, by ID
	 */
	public final Map<Integer, Keybind> keybinds = new ConcurrentHashMap<>();

	/**
	 * @param launcher
	 */
	public LWJGLKeybindManager(GameLauncher launcher) {
		this.launcher = launcher;
	}

	@Override
	public Keybind createKeybind(int keybind) {
		loadName(keybind);
		loadKeybind(keybind);
		return keybinds.get(keybind);
	}

	@Override
	public void post(GameFunction<Keybind, KeybindEntry> entry) throws GameException {
		KeybindEntry ke = entry.apply(AllKeybind.ALL.get());
		if (ke != null) {
			launcher.getEventManager().post(new KeybindEntryEvent(ke));
		}
		for (Keybind keybind : keybinds.values()) {
			ke = entry.apply(keybind);
			if (ke != null) {
				keybind.handle(ke);
			}
		}
	}

	@Override
	protected void cleanup0() throws GameException {
	}

	private void loadKeybind(int id) {
		if (!keybinds.containsKey(id)) {
			String name = names.get(id);
			Keybind keybind = new LWJGLKeybind(name, id, this);
			keybinds.put(id, keybind);
		}
	}

	private void loadName(int id) {
		if (!names.containsKey(id)) {
			if (id >= KEYBOARD_ADD && id < SCROLL) {
				names.put(id, glfwGetKeyName(id - KEYBOARD_ADD, 0));
			} else if (id == SCROLL) {
				names.put(id, "MouseWheel");
			} else if (id >= MOUSE_ADD && id < KEYBOARD_SCANCODE_ADD) {
				names.put(id, MOUSEBUTTON_PREFIX + (id - MOUSE_ADD));
			} else if (id > KEYBOARD_SCANCODE_ADD) {
				names.put(id, glfwGetKeyName(0, id - KEYBOARD_SCANCODE_ADD));
			} else {
				throw new IllegalArgumentException("Key: " + id);
			}
		}
	}
}
