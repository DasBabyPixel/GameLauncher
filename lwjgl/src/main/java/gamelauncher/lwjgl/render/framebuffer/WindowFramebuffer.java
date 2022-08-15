package gamelauncher.lwjgl.render.framebuffer;

import gamelauncher.engine.render.Window;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public class WindowFramebuffer extends AbstractFramebuffer {

	/**
	 * @param window
	 */
	public WindowFramebuffer(Window window) {
		super(window);
	}

	@Override
	public void cleanup() throws GameException {
		super.cleanup();
	}

	@Override
	public void beginFrame() {
	}

	@Override
	public void endFrame() {
	}

}
