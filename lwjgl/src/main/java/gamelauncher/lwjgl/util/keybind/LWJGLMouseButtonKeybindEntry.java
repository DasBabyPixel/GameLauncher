package gamelauncher.lwjgl.util.keybind;

import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEntry;

/**
 * @author DasBabyPixel
 */
public class LWJGLMouseButtonKeybindEntry extends AbstractKeybindEntry implements MouseButtonKeybindEntry {

	private final float mouseX;
	private final float mouseY;
	private final Type type;
	
	/**
	 * @param keybind
	 * @param mouseX
	 * @param mouseY
	 * @param type
	 */
	public LWJGLMouseButtonKeybindEntry(Keybind keybind, float mouseX, float mouseY, Type type) {
		super(keybind);
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.type = type;
	}

	@Override
	public float mouseX() {
		return mouseX;
	}

	@Override
	public float mouseY() {
		return mouseY;
	}

	@Override
	public Type type() {
		return type;
	}

	@Override
	public MouseButtonKeybindEntry withType(Type type) {
		return new LWJGLMouseButtonKeybindEntry(getKeybind(), mouseX, mouseY, type);
	}
}
