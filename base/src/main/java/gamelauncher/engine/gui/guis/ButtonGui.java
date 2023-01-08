package gamelauncher.engine.gui.guis;

import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.launcher.ColorGui;
import gamelauncher.engine.render.font.Font;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.InterpolatedColor;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEntry;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEntry.Type;
import gamelauncher.engine.util.property.PropertyVector4f;

import java.util.concurrent.TimeUnit;

/**
 * @author DasBabyPixel
 */
public class ButtonGui extends ParentableAbstractGui {

	private final Property<String> text;
	private final Property<Font> font;
	private final InterpolatedColor textColor;
	private final InterpolatedColor backgroundColor;
	private final BooleanValue pressing = BooleanValue.falseValue();

	public ButtonGui(GameLauncher launcher) throws GameException {
		super(launcher);
		this.setWidth(100);
		this.setHeight(50);
		this.textColor = new InterpolatedColor();
		this.backgroundColor = new InterpolatedColor();

		ColorGui colorGui = launcher.getGuiManager().createGui(ColorGui.class);
		colorGui.getXProperty().bind(this.getXProperty());
		colorGui.getYProperty().bind(this.getYProperty());
		colorGui.getWidthProperty().bind(this.getWidthProperty());
		colorGui.getHeightProperty().bind(this.getHeightProperty());
		colorGui.getColor().bind(backgroundColor.currentColor());
		this.GUIs.add(colorGui);

		TextGui textGui = new TextGui(launcher, "no text set", 50);
		textGui.getXProperty().bind(this.getXProperty().add(this.getWidthProperty().divide(2))
				.subtract(textGui.getWidthProperty().divide(2)));
		textGui.getYProperty().bind(this.getYProperty().add(this.getHeightProperty().divide(2))
				.subtract(textGui.getHeightProperty().divide(2)));
		textGui.getHeightProperty().bind(this.getHeightProperty());
		textGui.color().bind(textColor.currentColor());
		Runnable recalc = () -> {
			if (this.hovering().booleanValue() || this.pressing.booleanValue()) {
				textColor.setDesired(new PropertyVector4f(0.7F, 0.7F, 0.7F, 1F),
						TimeUnit.MILLISECONDS.toNanos(50));
				backgroundColor.setDesired(new PropertyVector4f(0.1F, 0, 0, 0.8F),
						TimeUnit.MICROSECONDS.toNanos(150));
			} else {
				textColor.setDesired(new PropertyVector4f(1F, 1F, 1F, 1F),
						TimeUnit.MILLISECONDS.toNanos(150));
				backgroundColor.setDesired(new PropertyVector4f(0F, 0, 0, 0.8F),
						TimeUnit.MICROSECONDS.toNanos(250));
			}
		};
		this.pressing.addListener(p -> recalc.run());
		this.hovering().addListener((p, o, n) -> recalc.run());
		recalc.run();
		this.text = textGui.text();
		this.font = textGui.font();
		this.GUIs.add(textGui);
	}

	@Override
	protected boolean doHandle(KeybindEntry entry) throws GameException {
		if (entry instanceof MouseButtonKeybindEntry mb) {
			if (mb.type() == Type.RELEASE) {
				if (isHovering(mb.mouseX(), mb.mouseY())) {
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

	@Override
	protected void doUpdate() throws GameException {
		if (textColor.calculateCurrent() | backgroundColor.calculateCurrent()) {
			redraw();
		}
	}

	/**
	 * @return the text property
	 */
	public Property<String> text() {
		return this.text;
	}

	public Property<Font> font() {
		return font;
	}

	protected void buttonPressed(MouseButtonKeybindEntry e) throws GameException {}

}
