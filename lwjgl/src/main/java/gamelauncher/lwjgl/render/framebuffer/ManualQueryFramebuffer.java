package gamelauncher.lwjgl.render.framebuffer;

import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.InvalidationListener;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.RenderThread;
import gamelauncher.engine.util.GameException;

@SuppressWarnings("javadoc")
public class ManualQueryFramebuffer extends AbstractFramebuffer {

	private final Framebuffer handle;

	private final BooleanValue newValue = BooleanValue.trueValue();

	public ManualQueryFramebuffer(Framebuffer handle, RenderThread renderThread) {
		super(renderThread, handle::scheduleRedraw);
		this.handle = handle;
		InvalidationListener invalid = property -> ManualQueryFramebuffer.this.newValue.setValue(true);
		handle.width().addListener(invalid);
		handle.height().addListener(invalid);
		this.query();
	}

	public ManualQueryFramebuffer(Framebuffer handle) {
		this(handle, handle.renderThread());
	}

	public void query() {
		this.newValue.setValue(false);
		this.width().setNumber(this.handle.width().getNumber());
		this.height().setNumber(this.handle.height().getNumber());
	}

	public BooleanValue newValue() {
		return this.newValue;
	}

	@Override
	public void cleanup0() throws GameException {
		super.cleanup0();
//		this.handle.cleanup();
	}

	@Override
	public void beginFrame() {
		this.handle.beginFrame();
	}

	@Override
	public void endFrame() {
		this.handle.endFrame();
	}

}
