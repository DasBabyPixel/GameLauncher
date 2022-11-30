package gamelauncher.engine.gui.guis;

import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.launcher.gui.ColorGui;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEntry;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEntry.Type;
import gamelauncher.engine.util.property.PropertyVector4f;

/**
 * @author DasBabyPixel
 */
public class ButtonGui extends ParentableAbstractGui {

	private final Property<String> text;

	private final BooleanValue pressing = BooleanValue.falseValue();

	/**
	 * @param launcher
	 * @throws GameException
	 */
	public ButtonGui(GameLauncher launcher) throws GameException {
		super(launcher);
		this.setWidth(100);
		this.setHeight(50);

		ColorGui colorGui = launcher.getGuiManager().createGui(ColorGui.class);
		colorGui.getXProperty().bind(this.getXProperty());
		colorGui.getYProperty().bind(this.getYProperty());
		colorGui.getWidthProperty().bind(this.getWidthProperty());
		colorGui.getHeightProperty().bind(this.getHeightProperty());
		colorGui.getColor().set(0, 0, 0, 0.8F);
		this.GUIs.add(colorGui);

		TextGui textGui = new TextGui(launcher, "no text set", 50);
		textGui.getXProperty().bind(this.getXProperty().add(this.getWidthProperty().divide(2))
				.subtract(textGui.getWidthProperty().divide(2)));
		textGui.getYProperty().bind(this.getYProperty().add(this.getHeightProperty().divide(2))
				.subtract(textGui.getHeightProperty().divide(2)));
		textGui.getHeightProperty().bind(this.getHeightProperty());
		Runnable recalc = () -> {
			PropertyVector4f c = textGui.color();
			if (this.mouseInsideGui().booleanValue() || this.pressing.booleanValue()) {
				c.set(0.8F, 0.8F, 0.8F, 1);
			} else {
				c.set(1, 1, 1, 1);
			}
		};
		this.pressing.addListener(p -> recalc.run());
		this.mouseInsideGui().addListener(p -> recalc.run());
		this.text = textGui.text();
		this.GUIs.add(textGui);

	}

	@Override
	protected boolean doHandle(KeybindEntry entry) throws GameException {
		if (entry instanceof MouseButtonKeybindEntry mb) {
			if (mb.type() == Type.RELEASE) {
				if (this.getX() < mb.mouseX() && this.getY() < mb.mouseY()
						&& this.getX() + this.getWidth() > mb.mouseX()
						&& this.getY() + this.getHeight() > mb.mouseY()) {
					try {
						this.buttonPressed(mb);
					} catch (GameException ex) {
						this.pressing.setValue(false);
						throw ex;
					}
				}
				this.pressing.setValue(false);
			} else if (mb.type() == Type.PRESS) {
				this.pressing.setValue(true);
			}
		}
		return super.doHandle(entry);
	}

	/**
	 * @return the text property
	 */
	public Property<String> text() {
		return this.text;
	}

	protected void buttonPressed(MouseButtonKeybindEntry e) throws GameException {}

}
