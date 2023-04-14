package gamelauncher.engine.util.keybind;

/**
 * @author DasBabyPixel
 */
public interface ScrollKeybindEvent extends KeybindEvent {

    /**
     * @return mouseX
     */
    float deltaX();

    /**
     * @return mouseY
     */
    float deltaY();

}
