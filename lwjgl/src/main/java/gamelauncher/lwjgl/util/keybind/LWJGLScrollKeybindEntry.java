package gamelauncher.lwjgl.util.keybind;

import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.ScrollKeybindEntry;

/**
 * @author DasBabyPixel
 */
public class LWJGLScrollKeybindEntry extends AbstractKeybindEntry implements ScrollKeybindEntry {

	private final float deltaX;
	private final float deltaY;

	/**
	 * @param keybind
	 * @param deltaX
	 * @param deltaY
	 */
	public LWJGLScrollKeybindEntry(Keybind keybind, float deltaX, float deltaY) {
		super(keybind);
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}

	@Override
	public float deltaX() {
		return deltaX;
	}

	@Override
	public float deltaY() {
		return deltaY;
	}
}
