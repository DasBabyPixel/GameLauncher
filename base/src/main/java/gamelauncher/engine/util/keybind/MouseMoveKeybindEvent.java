package gamelauncher.engine.util.keybind;

/**
 * @author DasBabyPixel
 */
public interface MouseMoveKeybindEvent extends KeybindEvent {

    /**
     * @return oldMouseX
     */
    float oldMouseX();

    /**
     * @return oldMouseY
     */
    float oldMouseY();

    /**
     * @return mouseX
     */
    float mouseX();

    /**
     * @return mouseY
     */
    float mouseY();

}
