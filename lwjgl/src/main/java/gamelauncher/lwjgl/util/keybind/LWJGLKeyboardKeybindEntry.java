package gamelauncher.lwjgl.util.keybind;

import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.KeyboardKeybindEntry;

/**
 * @author DasBabyPixel
 */
public class LWJGLKeyboardKeybindEntry extends AbstractKeybindEntry implements KeyboardKeybindEntry {

	private final Type type;

	/**
	 * @param keybind
	 * @param type
	 */
	public LWJGLKeyboardKeybindEntry(Keybind keybind, Type type) {
		super(keybind);
		this.type = type;
	}

	@Override
	public Type type() {
		return type;
	}
}
