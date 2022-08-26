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
		setWidth(100);
		setHeight(50);

		ColorGui colorGui = launcher.getGuiManager().createGui(ColorGui.class);
		colorGui.getXProperty().bind(getXProperty());
		colorGui.getYProperty().bind(getYProperty());
		colorGui.getWidthProperty().bind(getWidthProperty());
		colorGui.getHeightProperty().bind(getHeightProperty());
		colorGui.getColor().set(0, 0, 0, 0.8F);
		GUIs.add(colorGui);

		TextGui textGui = new TextGui(launcher, "no text set", 50);
		textGui.getXProperty()
				.bind(getXProperty().add(getWidthProperty().divide(2)).subtract(textGui.getWidthProperty().divide(2)));
		textGui.getYProperty()
				.bind(getYProperty().add(getHeightProperty().divide(2))
						.subtract(textGui.getHeightProperty().divide(2)));
		textGui.getHeightProperty().bind(getHeightProperty());
		Runnable recalc = () -> {
			PropertyVector4f c = textGui.color();
			if (mouseInsideGui().booleanValue() || pressing.booleanValue()) {
				c.set(0.8F, 0.8F, 0.8F, 1);
			} else {
				c.set(1, 1, 1, 1);
			}
		};
		pressing.addListener(p -> recalc.run());
		mouseInsideGui().addListener(p -> recalc.run());
		this.text = textGui.text();
		this.GUIs.add(textGui);

	}

	@Override
	protected boolean doHandle(KeybindEntry entry) throws GameException {
		if (entry instanceof MouseButtonKeybindEntry) {
			MouseButtonKeybindEntry mb = (MouseButtonKeybindEntry) entry;
			if (mb.type() == Type.RELEASE) {
				if (getX() < mb.mouseX() && getY() < mb.mouseY() && getX() + getWidth() > mb.mouseX()
						&& getY() + getHeight() > mb.mouseY()) {
					buttonPressed(mb);
				}
				pressing.setValue(false);
			} else if (mb.type() == Type.PRESS) {
				pressing.setValue(true);
			}
		}
		return super.doHandle(entry);
	}

	/**
	 * @return the text property
	 */
	public Property<String> text() {
		return text;
	}

	protected void buttonPressed(MouseButtonKeybindEntry e) {
	}

}
