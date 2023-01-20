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

	protected final Runnable draw;
	private final NumberValue width = NumberValue.zero();
	private final NumberValue height = NumberValue.zero();
	private final RenderThread renderThread;
	private final ScissorStack scissor;

	public AbstractFramebuffer(Frame frame) {
		this(frame.framebuffer().renderThread(), frame::scheduleDraw);
	}

	public AbstractFramebuffer(RenderThread render, Runnable draw) {
		this.renderThread = render;
		this.draw = draw;
		this.scissor = new LWJGLScissorStack(this);
	}

	@Override
	protected void cleanup0() throws GameException {
		LWJGLGuiManager lgm =
				(LWJGLGuiManager) this.renderThread.getFrame().launcher().guiManager();
		lgm.cleanup(this);
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

	@Override
	public ScissorStack scissorStack() {
		return this.scissor;
	}

	@Override
	public void scheduleRedraw() {
		this.draw.run();
	}

}
