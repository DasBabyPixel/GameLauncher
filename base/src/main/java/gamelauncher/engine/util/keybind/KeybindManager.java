package gamelauncher.engine.util.keybind;

import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameFunction;

/**
 * @author DasBabyPixel
 */
public interface KeybindManager extends GameResource {

	/**
	 * @param keybind
	 * @return the created {@link Keybind}
	 */
	Keybind createKeybind(int keybind);

	/**
	 * Posts a {@link KeybindEntry} to all Keybinds. Return null in the function to
	 * ignore this keybind. Should always return true for the {@link Keybind
	 * AllKeybind}
	 * 
	 * @param entry
	 * @throws GameException
	 */
	void post(GameFunction<Keybind, KeybindEntry> entry) throws GameException;

}
