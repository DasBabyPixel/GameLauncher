package gamelauncher.engine.util.keybind;

import de.dasbabypixel.annotations.Api;

/**
 * @author DasBabyPixel
 */
public interface KeyboardKeybindEvent extends KeybindEvent {

    /**
     * @return the {@link Type} of this {@link KeyboardKeybindEvent}
     */
    Type type();

    /**
     * @author DasBabyPixel
     */
    enum Type {
        PRESS, RELEASE, HOLD, REPEAT, CHARACTER
    }

    interface CharacterKeybindEvent extends KeyboardKeybindEvent {
        @Api
        char character();
    }
}
