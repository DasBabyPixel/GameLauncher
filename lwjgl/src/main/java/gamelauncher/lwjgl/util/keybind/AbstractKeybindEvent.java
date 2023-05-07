package gamelauncher.lwjgl.util.keybind;

import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.KeybindEvent;

/**
 * @author DasBabyPixel
 */
public abstract class AbstractKeybindEvent implements KeybindEvent {

    private final Keybind keybind;
    private boolean consumed = false;

    public AbstractKeybindEvent(Keybind keybind) {
        this.keybind = keybind;
    }

    @Override public Keybind keybind() {
        return keybind;
    }

    @Override public boolean consumed() {
        return consumed;
    }

    @Override public void consume() {
        if (consumed) throw new IllegalStateException("Already consumed!");
        consumed = true;
    }
}
