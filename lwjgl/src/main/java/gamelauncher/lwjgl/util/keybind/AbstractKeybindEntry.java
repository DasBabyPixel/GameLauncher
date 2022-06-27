package gamelauncher.lwjgl.util.keybind;

import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.KeybindEntry;

/**
 * @author DasBabyPixel
 */
public abstract class AbstractKeybindEntry implements KeybindEntry {

	private final Keybind keybind;

	/**
	 * @param keybind
	 */
	public AbstractKeybindEntry(Keybind keybind) {
		this.keybind = keybind;
	}

	@Override
	public Keybind getKeybind() {
		return keybind;
	}
}