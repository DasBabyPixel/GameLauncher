package gamelauncher.engine.util.keybind;

/**
 * @author DasBabyPixel
 */
public interface KeybindEvent {

    /**
     * @return the {@link Keybind} of this {@link KeybindEvent}
     */
    Keybind keybind();

    /**
     * @return whether this event is consumed. If it is consumed, no more GUIs may receive this event.
     */
    boolean consumed();

    /**
     * Consumes this event. No more GUIs may receive this event.
     */
    void consume();

}
