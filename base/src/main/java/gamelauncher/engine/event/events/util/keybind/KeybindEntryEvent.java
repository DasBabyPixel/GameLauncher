package gamelauncher.engine.event.events.util.keybind;

import gamelauncher.engine.event.Event;
import gamelauncher.engine.util.keybind.KeybindEvent;

/**
 * @author DasBabyPixel
 */
public class KeybindEntryEvent extends Event {

    private final KeybindEvent entry;

    public KeybindEntryEvent(KeybindEvent entry) {
        this.entry = entry;
    }

    /**
     * @return the KeybindEntry of the event
     */
    public KeybindEvent entry() {
        return entry;
    }
}
