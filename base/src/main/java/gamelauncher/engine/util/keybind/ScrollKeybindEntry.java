package gamelauncher.engine.util.keybind;

/**
 * @author DasBabyPixel
 */
public interface ScrollKeybindEntry extends KeybindEntry {

	/**
	 * @return mouseX
	 */
	float deltaX();
	
	/**
	 * @return mouseY
	 */
	float deltaY();
	
}
