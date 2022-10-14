package gamelauncher.lwjgl.render.framebuffer;

import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.InvalidationListener;
import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.util.GameException;

@SuppressWarnings("javadoc")
public class ManualQueryFramebuffer extends AbstractFramebuffer {

	private final Framebuffer handle;

	private final BooleanValue newValue = BooleanValue.trueValue();

	public ManualQueryFramebuffer(Framebuffer handle) {
		super(handle.renderThread(), handle::scheduleRedraw);
		this.handle = handle;
		InvalidationListener invalid = new InvalidationListener() {

			@Override
			public void invalidated(Property<?> property) {
				ManualQueryFramebuffer.this.newValue.setValue(true);
			}

		};
		handle.width().addListener(invalid);
		handle.height().addListener(invalid);
		this.query();
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
		this.handle.cleanup();
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
