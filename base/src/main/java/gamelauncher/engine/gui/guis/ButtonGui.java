package gamelauncher.engine.gui.guis;

import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.InterpolatedColor;
import gamelauncher.engine.util.keybind.KeybindEvent;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEvent;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEvent.Type;
import gamelauncher.engine.util.property.PropertyVector4f;
import gamelauncher.engine.util.text.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author DasBabyPixel
 */
public class ButtonGui extends ParentableAbstractGui {

    private final Property<Component> text;
    private final InterpolatedColor textColor;
    private final InterpolatedColor backgroundColor;
    private final BooleanValue pressing = BooleanValue.falseValue();

    public ButtonGui(GameLauncher launcher) throws GameException {
        super(launcher);
        this.width(100);
        this.height(50);
        this.textColor = new InterpolatedColor();
        this.backgroundColor = new InterpolatedColor();

        ColorGui colorGui = launcher.guiManager().createGui(ColorGui.class);
        colorGui.xProperty().bind(this.xProperty());
        colorGui.yProperty().bind(this.yProperty());
        colorGui.widthProperty().bind(this.widthProperty());
        colorGui.heightProperty().bind(this.heightProperty());
        colorGui.color().bind(backgroundColor.currentColor());
        this.GUIs.add(colorGui);

        TextGui textGui = new TextGui(launcher, Component.text(getClass().getSimpleName()), 50);
        textGui.xProperty().bind(this.xProperty().add(this.widthProperty().divide(2))
                .subtract(textGui.widthProperty().divide(2)));
        textGui.yProperty().bind(this.yProperty().add(this.heightProperty().divide(2))
                .subtract(textGui.heightProperty().divide(2)));
        textGui.heightProperty().bind(this.heightProperty());
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
        this.GUIs.add(textGui);
    }

    @Override
    protected boolean doHandle(KeybindEvent entry) throws GameException {
        if (entry instanceof MouseButtonKeybindEvent) {
            MouseButtonKeybindEvent mb = (MouseButtonKeybindEvent) entry;
            if (mb.type() == Type.RELEASE) {
                if (hovering(mb.mouseX(), mb.mouseY())) {
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
    public Property<Component> text() {
        return this.text;
    }

    protected void buttonPressed(MouseButtonKeybindEvent e) throws GameException {
    }

}
