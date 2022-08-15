package gamelauncher.engine.gui;

import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public abstract class AbstractGui implements Gui {

	private final NumberValue x = NumberValue.zero();
	private final NumberValue y = NumberValue.zero();
	private final NumberValue w = NumberValue.zero();
	private final NumberValue h = NumberValue.zero();
	private final GameLauncher launcher;
	private final BooleanValue focused = BooleanValue.falseValue();

	/**
	 * @param launcher
	 */
	public AbstractGui(GameLauncher launcher) {
		this.launcher = launcher;
	}

	@Override
	public NumberValue getWidthProperty() {
		return w;
	}

	@Override
	public NumberValue getHeightProperty() {
		return h;
	}

	@Override
	public NumberValue getXProperty() {
		return x;
	}

	@Override
	public NumberValue getYProperty() {
		return y;
	}

	@Override
	public BooleanValue getFocusedProperty() {
		return focused;
	}

	@Override
	public GameLauncher getLauncher() {
		return launcher;
	}

	@Override
	public void focus() throws GameException {
		focused.setValue(true);
	}

	@Override
	public void unfocus() throws GameException {
		focused.setValue(false);
	}

	@Override
	public void render(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick) throws GameException {

	}

	@Override
	public void onClose() throws GameException {

	}

	@Override
	public void onOpen() throws GameException {

	}

	@Override
	public void init(Framebuffer framebuffer) throws GameException {

	}

	@Override
	public void cleanup(Framebuffer framebuffer) throws GameException {

	}

	@Override
	public void update() throws GameException {

	}
}
