package gamelauncher.engine.util.keybind;

import de.dasbabypixel.annotations.Api;

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
     * @return the button id. On PC 0 will be primary, 1 will be secondary.<br>
     * On Android it will be the <b>n</b>th finger to touch the screen
     */
    @Api int buttonId();

    /**
     * @return the cloned {@link MouseButtonKeybindEvent}
     */
    MouseButtonKeybindEvent withType(Type type);

    /**
     * @author DasBabyPixel
     */
    enum Type {
        PRESS, RELEASE, HOLD
    }
}
