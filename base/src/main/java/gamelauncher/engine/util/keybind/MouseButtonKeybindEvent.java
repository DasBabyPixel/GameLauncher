package gamelauncher.engine.util.keybind;

/**
 * @author DasBabyPixel
 */
public interface MouseButtonKeybindEvent extends KeybindEvent {

    /**
     * @return the mouseX
     */
    float mouseX();

    /**
     * @return the mouseY
     */
    float mouseY();

    /**
     * @return the Type
     */
    Type type();

    /**
     * @param type
     * @return the cloned {@link MouseButtonKeybindEvent}
     */
    MouseButtonKeybindEvent withType(Type type);

    /**
     * @author DasBabyPixel
     */
    public static enum Type {
        PRESS, RELEASE, HOLD
    }
}
