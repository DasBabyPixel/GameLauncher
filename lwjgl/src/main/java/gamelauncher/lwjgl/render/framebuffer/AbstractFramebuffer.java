package gamelauncher.lwjgl.render.framebuffer;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.render.Frame;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.RenderThread;
import gamelauncher.engine.render.ScissorStack;
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
	 * @param frame 
	 * @param window
	 */
	public AbstractFramebuffer(Frame frame) {
		this(frame.framebuffer().renderThread(), frame::scheduleDraw);
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
	protected void cleanup0() throws GameException {
		LWJGLGuiManager lgm = (LWJGLGuiManager) this.renderThread.getFrame().getLauncher().getGuiManager();
		lgm.cleanup(this);
	}

	@Override
	public void scheduleRedraw() {
		this.draw.run();
	}

	@Override
	public ScissorStack scissorStack() {
		return this.scissor;
	}

	@Override
	public NumberValue width() {
		return this.width;
	}

	@Override
	public NumberValue height() {
		return this.height;
	}

	@Override
	public RenderThread renderThread() {
		return this.renderThread;
	}

}
