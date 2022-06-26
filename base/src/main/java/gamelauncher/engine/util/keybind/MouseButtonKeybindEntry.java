package gamelauncher.engine.util.keybind;

/**
 * @author DasBabyPixel
 */
public interface MouseButtonKeybindEntry extends KeybindEntry {
	
	/**
	 * @return the mouseX
	 */
	float mouseX();
	
	/**
	 * @return the mouseY
	 */
	float mouseY();
	
	/**
	 * @return the Type
	 */
	Type type();
	
	/**
	 * @param type
	 * @return the cloned {@link MouseButtonKeybindEntry}
	 */
	MouseButtonKeybindEntry withType(Type type);

	/**
	 * @author DasBabyPixel
	 */
	@SuppressWarnings("javadoc")
	public static enum Type {
		PRESS, RELEASE, HOLD
	}
}
