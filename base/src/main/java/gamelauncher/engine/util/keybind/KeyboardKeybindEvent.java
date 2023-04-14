package gamelauncher.engine.util.keybind;

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
        char character();
    }
}
