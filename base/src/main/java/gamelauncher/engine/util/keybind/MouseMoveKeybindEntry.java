package gamelauncher.engine.util.keybind;

/**
 * @author DasBabyPixel
 */
public interface MouseMoveKeybindEntry extends KeybindEntry {

	/**
	 * @return oldMouseX
	 */
	float oldMouseX();
	
	/**
	 * @return oldMouseY
	 */
	float oldMouseY();
	
	/**
	 * @return mouseX
	 */
	float mouseX();
	
	/**
	 * @return mouseY
	 */
	float mouseY();
	
}
