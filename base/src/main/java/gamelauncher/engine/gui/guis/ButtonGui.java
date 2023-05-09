package gamelauncher.engine.gui.guis;

import de.dasbabypixel.annotations.Api;
import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.ChangeListener;
import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.InterpolatedColor;
import gamelauncher.engine.util.function.GameConsumer;
import gamelauncher.engine.util.keybind.KeybindEvent;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEvent;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEvent.Type;
import gamelauncher.engine.util.property.PropertyVector4f;
import gamelauncher.engine.util.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * @author DasBabyPixel
 */
public interface ButtonGui extends Gui {

    void onButtonPressed(@Nullable GameConsumer<MouseButtonKeybindEvent> consumer);

    /**
     * @return the property to set the foreground. Value may be null
     */
    @Api @NotNull Property<Gui> foreground();

    /**
     * @return the property to set the background. Value may be null
     */
    @Api @NotNull Property<Gui> background();

    @Api @NotNull BooleanValue pressing();

    @Api
    class Simple extends ParentableAbstractGui implements ButtonGui {

        private final Property<Gui> background;
        private final Property<Gui> foreground;
        private final BooleanValue pressing = BooleanValue.falseValue();
        private volatile @Nullable GameConsumer<MouseButtonKeybindEvent> buttonPressed = null;

        public Simple(GameLauncher launcher) throws GameException {
            super(launcher);
            this.width(100);
            this.height(50);
            background = Property.empty();
            foreground = Property.empty();
            background.addListener(Property::value);
            foreground.addListener(Property::value);
            ChangeListener<Gui> changeListener = (property, oldValue, newValue) -> {
                if (oldValue != null) {
                    oldValue.xProperty().unbind();
                    oldValue.yProperty().unbind();
                    oldValue.widthProperty().unbind();
                    oldValue.heightProperty().unbind();
                    GUIs.remove(oldValue);
                }
                if (newValue != null) {
                    newValue.xProperty().bind(xProperty());
                    newValue.yProperty().bind(yProperty());
                    newValue.widthProperty().bind(widthProperty());
                    newValue.heightProperty().bind(heightProperty());
                    GUIs.add(newValue);
                }
            };
            background.addListener(changeListener);
            foreground.addListener(changeListener);
            addGuis();
        }

        protected void addGuis() throws GameException {
            background.value(new Simple.ColorBackground(this));
            foreground.value(new TextForeground(this));
        }

        @Override protected boolean doHandle(KeybindEvent entry) throws GameException {
            if (entry instanceof MouseButtonKeybindEvent) {
                MouseButtonKeybindEvent mb = (MouseButtonKeybindEvent) entry;
                if (mb.type() == Type.RELEASE) {
                    if (hovering(mb.mouseX(), mb.mouseY())) {
                        try {
                            this.buttonPressed(mb);
                        } catch (GameException ex) {
                            this.pressing.value(false);
                            throw ex;
                        }
                    }
                    this.pressing.value(false);
                } else if (mb.type() == Type.PRESS) {
                    this.pressing.value(true);
                }
            }
            return super.doHandle(entry);
        }

        @Override public void onButtonPressed(@Nullable GameConsumer<MouseButtonKeybindEvent> consumer) {
            buttonPressed = consumer;
        }

        @NotNull @Override public Property<Gui> foreground() {
            return foreground;
        }

        @Override @NotNull public Property<Gui> background() {
            return background;
        }

        @Override @NotNull public BooleanValue pressing() {
            return pressing;
        }

        protected void buttonPressed(MouseButtonKeybindEvent event) throws GameException {
            GameConsumer<MouseButtonKeybindEvent> cons = buttonPressed;
            if (cons != null) cons.accept(event);
        }

        public static class ColorBackground extends ParentableAbstractGui {

            private final InterpolatedColor backgroundColor;

            public ColorBackground(ButtonGui buttonGui) throws GameException {
                super(buttonGui.launcher());
                this.backgroundColor = new InterpolatedColor();
                this.backgroundColor.set(new PropertyVector4f(0F, 0, 0, 0.8F));

                ColorGui colorGui = launcher().guiManager().createGui(ColorGui.class);
                colorGui.xProperty().bind(this.xProperty());
                colorGui.yProperty().bind(this.yProperty());
                colorGui.widthProperty().bind(this.widthProperty());
                colorGui.heightProperty().bind(this.heightProperty());
                colorGui.color().bind(backgroundColor.currentColor());
                this.GUIs.add(colorGui);
                Runnable recalc = () -> {
                    if (this.hovering().booleanValue() || buttonGui.pressing().booleanValue()) {
                        backgroundColor.setDesired(new PropertyVector4f(0.1F, 0.1F, 0.1F, 0.9F), TimeUnit.MILLISECONDS.toNanos(150));
                    } else {
                        backgroundColor.setDesired(new PropertyVector4f(0F, 0, 0, 0.8F), TimeUnit.MILLISECONDS.toNanos(250));
                    }
                };
                buttonGui.pressing().addListener(p -> recalc.run());
                this.hovering().addListener((p, o, n) -> recalc.run());
                recalc.run();
            }

            @Override protected void doUpdate() {
                if (backgroundColor.calculateCurrent()) redraw();
            }
        }

        public static class TextForeground extends ParentableAbstractGui {

            private final InterpolatedColor textColor;
            private final TextGui textGui;

            public TextForeground(ButtonGui buttonGui) throws GameException {
                super(buttonGui.launcher());
                this.textColor = new InterpolatedColor();
                this.textColor.set(new PropertyVector4f(1F, 1F, 1F, 1F));
                textGui = launcher().guiManager().createGui(TextGui.class);
                textGui.text().value(Component.text(getClass().getSimpleName()));
                textGui.xProperty().bind(this.xProperty().add(this.widthProperty().divide(2)).subtract(textGui.widthProperty().divide(2)));
                textGui.yProperty().bind(this.yProperty().add(this.heightProperty().divide(2)).subtract(textGui.heightProperty().divide(2)));
                textGui.heightProperty().bind(this.heightProperty());
                textGui.color().bind(textColor.currentColor());
                GUIs.add(textGui);

                Runnable recalc = () -> {
                    if (this.hovering().booleanValue() || buttonGui.pressing().booleanValue()) {
                        textColor.setDesired(new PropertyVector4f(0.7F, 0.7F, 0.7F, 1F), TimeUnit.MILLISECONDS.toNanos(50));
                    } else {
                        textColor.setDesired(new PropertyVector4f(1F, 1F, 1F, 1F), TimeUnit.MILLISECONDS.toNanos(150));
                    }
                };
                buttonGui.pressing().addListener(p -> recalc.run());
                this.hovering().addListener((p, o, n) -> recalc.run());
                recalc.run();
            }

            @Api public TextGui textGui() {
                return textGui;
            }

            @Override protected void doUpdate() {
                if (textColor.calculateCurrent()) redraw();
            }
        }
    }

}
