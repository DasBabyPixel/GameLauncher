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
		super(handle.getRenderThread(), handle::scheduleRedraw);
		this.handle = handle;
		InvalidationListener invalid = new InvalidationListener() {

			@Override
			public void invalidated(Property<?> property) {
				newValue.setValue(true);
			}

		};
		handle.width().addListener(invalid);
		handle.height().addListener(invalid);
	}

	public void query() {
		newValue.setValue(false);
		width().setNumber(handle.width().getNumber());
		height().setNumber(handle.height().getNumber());
	}

	@Override
	public void cleanup() throws GameException {
		super.cleanup();
		handle.cleanup();
	}

	@Override
	public void beginFrame() {
		handle.beginFrame();
	}

	@Override
	public void endFrame() {
		handle.endFrame();
	}

}
