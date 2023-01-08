package gamelauncher.engine.util.keybind;

/**
 * @author DasBabyPixel
 */
public interface KeyboardKeybindEntry extends KeybindEntry {

	/**
	 * @return the {@link Type} of this {@link KeyboardKeybindEntry}
	 */
	Type type();

	/**
	 * @author DasBabyPixel
	 */
	enum Type {
		PRESS, RELEASE, HOLD, REPEAT, CHARACTER
	}
}
