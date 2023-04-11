package gamelauncher.engine.util.keybind;

import gamelauncher.engine.resource.GameResource;

/**
 * @author DasBabyPixel
 *
 */
public interface Keybind extends GameResource {

	/**
	 * @return the name of this Keybind. For Display usage
	 */
	String name();

	/**
	 * @return the uniqueId of this Keybind. Can be used for storage, will be the
	 *         same for the same key. May vary on different devices
	 */
	int uniqueId();

	/**
	 * Handles a {@link KeybindEntry}
	 * 
	 * @param entry an entry
	 */
	void handle(KeybindEntry entry);

	/**
	 * Adds a {@link KeybindHandler}
	 * 
	 * @see Keybind#removeHandler(KeybindHandler)
	 * @see Keybind#handle(KeybindEntry)
	 * 
	 * @param handler a handler
	 */
	void addHandler(KeybindHandler handler);

	/**
	 * Removes a {@link KeybindHandler}
	 * 
	 * @see Keybind#addHandler(KeybindHandler)
	 * @see Keybind#handle(KeybindEntry)
	 * @param handler a handler
	 */
	void removeHandler(KeybindHandler handler);

	/**
	 * @return the {@link KeybindManager}
	 */
	KeybindManager manager();

}
