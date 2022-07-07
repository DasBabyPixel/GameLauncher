package gamelauncher.engine.util.keybind;

import gamelauncher.engine.util.function.GameResource;

/**
 * @author DasBabyPixel
 *
 */
public interface Keybind extends GameResource {

	/**
	 * @return the name of this Keybind. For Display usage
	 */
	String getName();

	/**
	 * @return the uniqueId of this Keybind. Can be used for storage, will be the
	 *         same for the same key.
	 */
	int getUniqueId();

	/**
	 * Handles a {@link KeybindEntry}
	 * 
	 * @param entry
	 */
	void handle(KeybindEntry entry);

	/**
	 * Adds a {@link KeybindHandler}
	 * 
	 * @see Keybind#removeHandler(KeybindHandler)
	 * @see Keybind#handle(KeybindEntry)
	 * 
	 * @param handler
	 */
	void addHandler(KeybindHandler handler);

	/**
	 * Removes a {@link KeybindHandler}
	 * 
	 * @see Keybind#addHandler(KeybindHandler)
	 * @see Keybind#handle(KeybindEntry)
	 * @param handler
	 */
	void removeHandler(KeybindHandler handler);

	/**
	 * @return the {@link KeybindManager}
	 */
	KeybindManager getManager();

}
