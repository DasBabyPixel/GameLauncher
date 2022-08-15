package gamelauncher.lwjgl.render.framebuffer;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.RenderThread;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.gui.LWJGLGuiManager;

/**
 * @author DasBabyPixel
 */
public abstract class AbstractFramebuffer implements Framebuffer {

	private final NumberValue width = NumberValue.zero();

	private final NumberValue height = NumberValue.zero();

	private final RenderThread renderThread;

	private final Runnable draw;

	/**
	 * @param window
	 */
	public AbstractFramebuffer(Window window) {
		this(window.getRenderThread(), window::scheduleDraw);
	}

	/**
	 * @param render
	 * @param draw
	 */
	public AbstractFramebuffer(RenderThread render, Runnable draw) {
		this.renderThread = render;
		this.draw = draw;
	}

	@Override
	public void cleanup() throws GameException {
		LWJGLGuiManager lgm = (LWJGLGuiManager) renderThread.getWindow().getLauncher().getGuiManager();
		lgm.cleanup(this);
	}

	@Override
	public void scheduleRedraw() {
		draw.run();
	}

	@Override
	public NumberValue width() {
		return width;
	}

	@Override
	public NumberValue height() {
		return height;
	}

	@Override
	public RenderThread getRenderThread() {
		return renderThread;
	}

}
