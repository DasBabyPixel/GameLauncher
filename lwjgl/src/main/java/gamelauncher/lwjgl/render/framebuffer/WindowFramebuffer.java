package gamelauncher.lwjgl.render.framebuffer;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public class WindowFramebuffer implements Framebuffer {

	private final NumberValue width = NumberValue.zero();
	private final NumberValue height = NumberValue.zero();

	@Override
	public void cleanup() throws GameException {
	}

	@Override
	public void beginFrame() {
	}

	@Override
	public void endFrame() {
	}

	@Override
	public NumberValue width() {
		return width;
	}

	@Override
	public NumberValue height() {
		return height;
	}
}
