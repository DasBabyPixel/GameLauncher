package gamelauncher.engine.util.keybind;

import gamelauncher.engine.resource.GameResource;

/**
 * @author DasBabyPixel
 */
public interface Keybind extends GameResource {

    /**
     * @return the name of this Keybind. For Display usage
     */
    String name();

    /**
     * @return the uniqueId of this Keybind. Can be used for storage, will be the
     * same for the same key. May vary on different devices
     */
    int uniqueId();

    /**
     * Handles a {@link KeybindEvent}
     *
     * @param entry an entry
     */
    void handle(KeybindEvent entry);

    /**
     * Adds a {@link KeybindHandler}
     *
     * @param handler a handler
     * @see Keybind#removeHandler(KeybindHandler)
     * @see Keybind#handle(KeybindEvent)
     */
    void addHandler(KeybindHandler handler);

    /**
     * Removes a {@link KeybindHandler}
     *
     * @param handler a handler
     * @see Keybind#addHandler(KeybindHandler)
     * @see Keybind#handle(KeybindEvent)
     */
    void removeHandler(KeybindHandler handler);

    /**
     * @return the {@link KeybindManager}
     */
    KeybindManager manager();

}
