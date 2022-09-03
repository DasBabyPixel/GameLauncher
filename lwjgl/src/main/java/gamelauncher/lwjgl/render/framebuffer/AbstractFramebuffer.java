package gamelauncher.lwjgl.render.framebuffer;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.RenderThread;
import gamelauncher.engine.render.ScissorStack;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.gui.LWJGLGuiManager;
import gamelauncher.lwjgl.render.LWJGLScissorStack;

/**
 * @author DasBabyPixel
 */
public abstract class AbstractFramebuffer extends AbstractGameResource implements Framebuffer {

	private final NumberValue width = NumberValue.zero();

	private final NumberValue height = NumberValue.zero();

	private final RenderThread renderThread;

	private final Runnable draw;

	private final ScissorStack scissor;

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
		this.scissor = new LWJGLScissorStack(this);
	}

	@Override
	public void cleanup0() throws GameException {
		LWJGLGuiManager lgm = (LWJGLGuiManager) renderThread.getWindow().getLauncher().getGuiManager();
		lgm.cleanup(this);
	}

	@Override
	public void scheduleRedraw() {
		draw.run();
	}

	@Override
	public ScissorStack scissorStack() {
		return scissor;
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
