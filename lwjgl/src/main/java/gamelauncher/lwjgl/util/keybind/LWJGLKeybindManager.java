package gamelauncher.lwjgl.util.keybind;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.event.events.util.keybind.KeybindEntryEvent;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.KeybindEvent;
import gamelauncher.engine.util.keybind.KeybindManager;
import java8.util.concurrent.CompletableFuture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.glfw.GLFW.glfwGetKeyName;

/**
 * @author DasBabyPixel
 */
public class LWJGLKeybindManager extends AbstractGameResource implements KeybindManager {

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
    public static final int KEYBOARD_SCANCODE_ADD = 200000;
    public static final int KEYBOARD_CODEPOINT_ADD = 20000;
    private static final String MOUSEBUTTON_PREFIX = "Button ";
    /**
     * The Map of {@link Keybind}s, by ID
     */
    private final Map<Integer, Keybind> keybinds = new ConcurrentHashMap<>();
    private final GameLauncher launcher;
    private final Map<Integer, String> names = new ConcurrentHashMap<>();

    public LWJGLKeybindManager(GameLauncher launcher) {
        this.launcher = launcher;
    }

    @Override public Keybind keybind(int keybind) {
        loadName(keybind);
        loadKeybind(keybind);
        return keybinds.get(keybind);
    }

    @Override public void post(KeybindEvent event) {
        launcher.eventManager().post(new KeybindEntryEvent(event));
        event.keybind().handle(event);
    }

    @Override protected CompletableFuture<Void> cleanup0() throws GameException {
        for (Keybind k : keybinds.values()) {
            k.cleanup();
        }
        keybinds.clear();
        return CompletableFuture.completedFuture(null);
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
                String n1 = glfwGetKeyName(id - KEYBOARD_ADD, 0);
                if (n1 == null) n1 = Character.toString(id);
                names.put(id, n1);
            } else if (id == SCROLL) {
                names.put(id, "MouseWheel");
            } else if (id >= MOUSE_ADD && id < KEYBOARD_CODEPOINT_ADD) {
                names.put(id, MOUSEBUTTON_PREFIX + (id - MOUSE_ADD));
            } else if (id >= KEYBOARD_CODEPOINT_ADD && id < KEYBOARD_SCANCODE_ADD) {
                names.put(id, Character.toString(id - KEYBOARD_CODEPOINT_ADD));
            } else if (id >= KEYBOARD_SCANCODE_ADD) {
                names.put(id, glfwGetKeyName(0, id - KEYBOARD_SCANCODE_ADD));
            } else {
                throw new IllegalArgumentException("Key: " + id);
            }
        }
    }
}
