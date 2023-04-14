package gamelauncher.lwjgl.gui.launcher;

import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.NumberValue;
import de.dasbabypixel.api.property.Property;
import de.dasbabypixel.api.property.implementation.ObjectProperty;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.launcher.ColorGui;
import gamelauncher.engine.gui.launcher.ScrollGui;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.ScissorStack;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.InterpolatedColor;
import gamelauncher.engine.util.keybind.KeybindEvent;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEvent;
import gamelauncher.engine.util.keybind.MouseMoveKeybindEvent;
import gamelauncher.engine.util.keybind.ScrollKeybindEvent;
import gamelauncher.engine.util.math.Math;
import gamelauncher.engine.util.property.PropertyVector4f;
import gamelauncher.lwjgl.gui.launcher.LWJGLScrollGui.Scrollbar.Type;
import gamelauncher.lwjgl.util.keybind.LWJGLKeybindManager;

import java.util.concurrent.TimeUnit;

/**
 * @author DasBabyPixel
 */
public class LWJGLScrollGui extends ParentableAbstractGui implements ScrollGui {

    private final Property<Gui> gui;
    private final Scrollbar verticalScrollbar;
    private final Scrollbar horizontalScrollbar;
    private final NumberValue displayWidth;
    private final NumberValue displayHeight;
    private final NumberValue displayX;
    private final NumberValue displayY;
    private final NumberValue guiWidth;
    private final NumberValue guiHeight;
    private final NumberValue guiX;
    private final NumberValue guiY;

    //	private Gui currentGui;

    public LWJGLScrollGui(GameLauncher launcher) throws GameException {
        super(launcher);
        this.gui = ObjectProperty.empty();
        this.guiWidth = NumberValue.zero();
        this.guiHeight = NumberValue.zero();
        this.guiX = NumberValue.zero();
        this.guiY = NumberValue.zero();
        this.verticalScrollbar = new Scrollbar(Scrollbar.Type.VERTICAL);
        this.horizontalScrollbar = new Scrollbar(Scrollbar.Type.HORIZONTAL);
        this.displayWidth = this.widthProperty().mapToNumber(n -> {
            if (this.verticalScrollbar.visible.booleanValue()) {
                return n.floatValue() - this.verticalScrollbar.thickness.floatValue();
            }
            return n.floatValue();
        }).addDependencies(this.verticalScrollbar.visible, this.verticalScrollbar.thickness);
        this.displayHeight = this.heightProperty().mapToNumber(n -> {
            if (this.horizontalScrollbar.visible.booleanValue()) {
                return n.floatValue() - this.horizontalScrollbar.thickness.floatValue();
            }
            return n.floatValue();
        }).addDependencies(this.horizontalScrollbar.visible, this.horizontalScrollbar.thickness);

        this.gui.addListener((property, oldValue, newValue) -> {
            if (oldValue != null) {
                guiWidth.unbind();
                guiHeight.unbind();
                guiWidth.setNumber(0);
                guiHeight.setNumber(0);
                oldValue.xProperty().unbind();
                oldValue.yProperty().unbind();
                this.GUIs.remove(oldValue);
            }
            if (newValue != null) {
                guiWidth.bind(newValue.widthProperty());
                guiHeight.bind(newValue.heightProperty());
                newValue.xProperty().bind(guiX);
                newValue.yProperty().bind(guiY);
                this.GUIs.add(newValue);
            }
        });
        this.horizontalScrollbar.max.bind(this.guiWidth.subtract(this.displayWidth).max(0));
        this.verticalScrollbar.max.bind(this.guiHeight.subtract(this.displayHeight).max(0));
        this.displayX = this.xProperty();
        this.displayY = this.yProperty().mapToNumber(n -> {
            if (this.horizontalScrollbar.visible.booleanValue()) {
                return n.doubleValue() + this.horizontalScrollbar.thickness.doubleValue();
            }
            return n;
        }).addDependencies(this.horizontalScrollbar.visible, this.horizontalScrollbar.thickness);
        this.horizontalScrollbar.visible.bind(
                this.displayWidth.mapToBoolean(n -> n.floatValue() < this.guiWidth.floatValue()));
        this.verticalScrollbar.visible.bind(
                this.displayHeight.mapToBoolean(n -> n.floatValue() < this.guiHeight.floatValue()));
        this.guiX.bind(this.displayX.subtract(this.horizontalScrollbar.display));
        this.guiY.bind(this.displayY.add(this.displayHeight).subtract(this.guiHeight)
                .add(this.verticalScrollbar.display));

        ScrollbarGui verticalScrollbarGui =
                new ScrollbarGui(this.launcher(), this.guiWidth, this.guiHeight,
                        this.verticalScrollbar, this.displayWidth, this.displayHeight);
        verticalScrollbarGui.xProperty().bind(this.xProperty().add(this.displayWidth));
        verticalScrollbarGui.yProperty().bind(this.yProperty().mapToNumber(n -> {
            if (this.horizontalScrollbar.visible.booleanValue()) {
                return n.doubleValue() + this.horizontalScrollbar.thickness.doubleValue();
            }
            return n;
        }).addDependencies(this.horizontalScrollbar.visible, this.horizontalScrollbar.thickness));
        verticalScrollbarGui.widthProperty().bind(this.verticalScrollbar.thickness);
        verticalScrollbarGui.heightProperty().bind(this.displayHeight);
        this.GUIs.add(verticalScrollbarGui);

        ScrollbarGui horizontalScrollbarGui =
                new ScrollbarGui(this.launcher(), this.guiWidth, this.guiHeight,
                        this.horizontalScrollbar, this.displayWidth, this.displayHeight);
        horizontalScrollbarGui.xProperty().bind(this.xProperty());
        horizontalScrollbarGui.yProperty().bind(this.yProperty());
        horizontalScrollbarGui.widthProperty().bind(this.displayWidth);
        horizontalScrollbarGui.heightProperty().bind(this.horizontalScrollbar.thickness);
        this.GUIs.add(horizontalScrollbarGui);
    }

    @Override
    protected boolean doHandle(KeybindEvent entry) throws GameException {
        if (entry instanceof ScrollKeybindEvent) {
            ScrollKeybindEvent s = (ScrollKeybindEvent) entry;
            float mulx = this.displayWidth.floatValue() / 10;
            float muly = this.displayHeight.floatValue() / 5;
            float dx = s.deltaX();
            float dy = s.deltaY();
            this.horizontalScrollbar.desireProgress(
                    this.horizontalScrollbar.desiredProgress().floatValue() - dx * mulx,
                    TimeUnit.MILLISECONDS.toNanos(150));
            this.verticalScrollbar.desireProgress(
                    this.verticalScrollbar.desiredProgress().floatValue() - dy * muly,
                    TimeUnit.MILLISECONDS.toNanos(150));
            this.redraw();
            return false;
        }
        return super.doHandle(entry);
    }

    @Override
    protected void doCleanup(Framebuffer framebuffer) throws GameException {
    }

    @Override
    protected final boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY,
                                     float partialTick) throws GameException {
        ScissorStack scissor = framebuffer.scissorStack();
        final Gui cgui = this.gui.getValue();
        if (cgui != null) {
            scissor.pushScissor(displayX, displayY, displayWidth, displayHeight);
            cgui.render(framebuffer, mouseX, mouseY, partialTick);
            scissor.popScissor();
        }

        for (Gui gui : GUIs)
            if (gui != cgui) gui.render(framebuffer, mouseX, mouseY, partialTick);

        return false;
    }

    /**
     * @return the gui inside this scrollgui
     */
    @Override
    public Property<Gui> gui() {
        return this.gui;
    }

    /**
     * @author DasBabyPixel
     */
    public static class ScrollbarGui extends ParentableAbstractGui {

        private final Scrollbar scrollbar;
        private final NumberValue scrollbarIndent;
        private final NumberValue backgroundX;
        private final NumberValue backgroundY;
        private final NumberValue maxScrollbarWidth;
        private final NumberValue maxScrollbarHeight;
        private final NumberValue scrollbarWidth;
        private final NumberValue scrollbarHeight;
        private final NumberValue scrollbarX;
        private final NumberValue scrollbarY;
        private final BooleanValue highlight;
        private final PropertyVector4f backgroundColor =
                new PropertyVector4f(0.05F, 0.05F, 0.05F, 1F);
        private final PropertyVector4f scrollbarColor = new PropertyVector4f(0.3F, 0.3F, 0.3F, 1F);
        private final PropertyVector4f highlightBackgroundColor =
                new PropertyVector4f(0F, 0F, 0F, 1F);
        private final PropertyVector4f highlightScrollbarColor =
                new PropertyVector4f(0.5F, 0.5F, 0.5F, 1F);
        private final InterpolatedColor curBackgroundColor = new InterpolatedColor();
        private final InterpolatedColor curScrollbarColor = new InterpolatedColor();
        private final NumberValue guiWidth;
        private final NumberValue guiHeight;
        private final NumberValue displayWidth;
        private final NumberValue displayHeight;
        private final BooleanValue dragging;
        private float dragOffset = 0;

        private ScrollbarGui(GameLauncher launcher, NumberValue guiWidth, NumberValue guiHeight,
                             Scrollbar scrollbar, NumberValue displayWidth, NumberValue displayHeight)
                throws GameException {
            super(launcher);
            this.guiWidth = guiWidth;
            this.guiHeight = guiHeight;
            this.displayWidth = displayWidth;
            this.displayHeight = displayHeight;
            this.scrollbar = scrollbar;
            this.scrollbarIndent = NumberValue.constant(1);
            this.dragging = BooleanValue.falseValue();
            this.backgroundX = this.xProperty();
            this.backgroundY = this.yProperty();
            NumberValue backgroundWidth = this.widthProperty();
            NumberValue backgroundHeight = this.heightProperty();
            this.highlight = BooleanValue.falseValue();
            this.maxScrollbarWidth =
                    backgroundWidth.subtract(this.scrollbarIndent.multiply(2)).max(0);
            this.maxScrollbarHeight =
                    backgroundHeight.subtract(this.scrollbarIndent.multiply(2)).max(0);
            boolean hor = scrollbar.type == Type.HORIZONTAL;
            boolean ver = scrollbar.type == Type.VERTICAL;
            this.scrollbarWidth = ver
                    ? this.maxScrollbarWidth
                    : displayWidth.divide(guiWidth).multiply(this.maxScrollbarWidth)
                    .min(this.maxScrollbarWidth);
            this.scrollbarHeight = hor
                    ? this.maxScrollbarHeight
                    : displayHeight.divide(guiHeight).multiply(this.maxScrollbarHeight)
                    .min(this.maxScrollbarHeight);
            NumberValue progress = scrollbar.display.divide(scrollbar.max);

            if (ver) {
                this.scrollbarX = this.backgroundX.add(this.scrollbarIndent);
                this.scrollbarY = this.backgroundY.add(this.scrollbarIndent)
                        .add(this.maxScrollbarHeight.subtract(this.scrollbarHeight).subtract(
                                this.maxScrollbarHeight.subtract(this.scrollbarHeight)
                                        .multiply(progress)));
            } else if (hor) {
                this.scrollbarX = this.backgroundX.add(this.scrollbarIndent)
                        .add(this.maxScrollbarWidth.subtract(this.scrollbarWidth)
                                .multiply(progress));
                this.scrollbarY = this.backgroundY.add(this.scrollbarIndent);
            } else {
                throw new IllegalArgumentException();
            }
            ColorGui backgroundGui = launcher.guiManager().createGui(ColorGui.class);
            backgroundGui.xProperty().bind(this.backgroundX);
            backgroundGui.yProperty().bind(this.backgroundY);
            backgroundGui.widthProperty().bind(backgroundWidth);
            backgroundGui.heightProperty().bind(backgroundHeight);
            this.curBackgroundColor.set(this.backgroundColor);
            PropertyVector4f guiBackgroundColor = backgroundGui.color();
            guiBackgroundColor.bind(this.curBackgroundColor.currentColor());
            this.GUIs.add(backgroundGui);

            ColorGui scrollbarGui = launcher.guiManager().createGui(ColorGui.class);
            scrollbarGui.xProperty().bind(this.scrollbarX);
            scrollbarGui.yProperty().bind(this.scrollbarY);
            scrollbarGui.widthProperty().bind(this.scrollbarWidth);
            scrollbarGui.heightProperty().bind(this.scrollbarHeight);
            this.curScrollbarColor.set(this.scrollbarColor);
            PropertyVector4f guiScrollbarColor = scrollbarGui.color();
            guiScrollbarColor.bind(this.curScrollbarColor.currentColor());
            this.GUIs.add(scrollbarGui);

            BooleanValue highlightOrDrag = this.highlight.or(this.dragging);
            highlightOrDrag.addListener(Property::getValue);
            highlightOrDrag.addListener(
                    (Property<? extends Boolean> property, Boolean oldValue, Boolean newValue) -> {
                        if (newValue) {
                            this.curBackgroundColor.setDesired(this.highlightBackgroundColor,
                                    TimeUnit.MILLISECONDS.toNanos(100));
                            this.curScrollbarColor.setDesired(this.highlightScrollbarColor,
                                    TimeUnit.MILLISECONDS.toNanos(100));
                        } else {
                            this.curBackgroundColor.setDesired(this.backgroundColor,
                                    TimeUnit.MILLISECONDS.toNanos(300));
                            this.curScrollbarColor.setDesired(this.scrollbarColor,
                                    TimeUnit.MILLISECONDS.toNanos(300));
                        }
                        this.redraw();
                    });
        }

        private void drag(MouseMoveKeybindEvent entry) {
            float newscrolled;
            if (this.scrollbar.type == Scrollbar.Type.VERTICAL) {
                float minsy = this.backgroundY.floatValue() + this.scrollbarIndent.floatValue();
                float maxsy = minsy + this.maxScrollbarHeight.floatValue()
                        - this.scrollbarHeight.floatValue();
                float newy = Math.clamp(entry.mouseY() - this.dragOffset, minsy, maxsy);
                // Invert because it's the vertical scrollbar
                float newlocaly =
                        (this.maxScrollbarHeight.floatValue() - this.scrollbarHeight.floatValue())
                                - (newy - minsy);
                float newpercentscrolled = newlocaly / (this.maxScrollbarHeight.floatValue()
                        - this.scrollbarHeight.floatValue());
                newscrolled = newpercentscrolled * (this.guiHeight.floatValue()
                        - this.displayHeight.floatValue());
            } else if (this.scrollbar.type == Scrollbar.Type.HORIZONTAL) {
                float minsx = this.backgroundX.floatValue() + this.scrollbarIndent.floatValue();
                float maxsx = minsx + this.maxScrollbarWidth.floatValue()
                        - this.scrollbarWidth.floatValue();
                float newx = Math.clamp(entry.mouseX() - this.dragOffset, minsx, maxsx);
                float newlocalx = newx - minsx;
                float newpercentscrolled = newlocalx / (this.maxScrollbarWidth.floatValue()
                        - this.scrollbarWidth.floatValue());
                newscrolled = newpercentscrolled * (this.guiWidth.floatValue()
                        - this.displayWidth.floatValue());
            } else {
                return;
            }
            this.scrollbar.desireProgress(newscrolled, 0);
            this.redraw();
        }

        @Override
        protected boolean doHandle(KeybindEvent entry) throws GameException {
            if (entry instanceof MouseMoveKeybindEvent) {
                MouseMoveKeybindEvent mm = (MouseMoveKeybindEvent) entry;
                if (Gui.hovering(this.scrollbarX.floatValue(), this.scrollbarY.floatValue(),
                        this.scrollbarWidth.floatValue(), this.scrollbarHeight.floatValue(),
                        mm.mouseX(), mm.mouseY())) {
                    if (!this.highlight.booleanValue()) {
                        this.highlight.setValue(true);
                    }
                } else if (this.highlight.booleanValue()) {
                    this.highlight.setValue(false);
                }
                if (this.dragging.booleanValue()) {
                    this.drag(mm);
                }
            } else if (entry instanceof MouseButtonKeybindEvent) {
                MouseButtonKeybindEvent mb = (MouseButtonKeybindEvent) entry;
                if (mb.type() == MouseButtonKeybindEvent.Type.PRESS) {
                    if (this.highlight.booleanValue()) {
                        if ((mb.keybind().uniqueId() - LWJGLKeybindManager.MOUSE_ADD) == 0) {
                            if (this.scrollbar.type == Scrollbar.Type.VERTICAL) {
                                this.dragOffset = mb.mouseY() - this.scrollbarY.floatValue();
                                this.dragging.setValue(true);
                            } else if (this.scrollbar.type == Scrollbar.Type.HORIZONTAL) {
                                this.dragOffset = mb.mouseX() - this.scrollbarX.floatValue();
                                this.dragging.setValue(true);
                            }
                        }
                    }
                } else if (mb.type() == MouseButtonKeybindEvent.Type.RELEASE) {
                    if (this.dragging.booleanValue()) {
                        if ((mb.keybind().uniqueId() - LWJGLKeybindManager.MOUSE_ADD) == 0) {
                            this.dragging.setValue(false);
                        }
                    }
                }
            }
            return super.doHandle(entry);
        }

        @Override
        protected void doUpdate() throws GameException {
            if (this.curScrollbarColor.calculateCurrent()
                    | this.curBackgroundColor.calculateCurrent()
                    | this.scrollbar.progress.calculateCurrent()) {
                this.redraw();
            }
        }

        @Override
        protected void preRender(Framebuffer framebuffer, float mouseX, float mouseY,
                                 float partialTick) throws GameException {
            this.curScrollbarColor.calculateCurrent();
            this.curBackgroundColor.calculateCurrent();
            this.scrollbar.progress.calculateCurrent();
        }

        @Override
        protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY,
                                   float partialTick) throws GameException {
            if (!this.scrollbar.visible.booleanValue()) {
                return false;
            }
            return super.doRender(framebuffer, mouseX, mouseY, partialTick);
        }

        /**
         * @return the scrollbar for this gui
         */
        public Scrollbar getScrollbar() {
            return this.scrollbar;
        }

    }

    private static class GradualProgress {

        private final NumberValue lastProgress = NumberValue.zero();

        private final NumberValue curProgress = NumberValue.zero();

        private final NumberValue desiredProgress = NumberValue.zero();

        private final NumberValue nanotimeStarted = NumberValue.zero();

        private final NumberValue nanotimeDone = NumberValue.zero();

        private void reset() {
            this.desiredProgress.setNumber((float) 0);
            this.lastProgress.setNumber((float) 0);
            this.curProgress.setNumber((float) 0);
            this.nanotimeDone.setNumber(System.nanoTime());
            this.nanotimeStarted.setNumber(System.nanoTime());
        }

        private boolean calculateCurrent() {
            long time = System.nanoTime();
            if (this.nanotimeDone.longValue() - time < 0) {
                if (this.curProgress.floatValue() == this.desiredProgress.floatValue()) {
                    return false;
                }
                this.curProgress.setNumber(this.desiredProgress.floatValue());
                return true;
            }
            long diff = this.nanotimeDone.longValue() - this.nanotimeStarted.longValue();
            if (diff == 0) {
                if (this.curProgress.floatValue() == this.desiredProgress.floatValue()) {
                    return false;
                }
                this.curProgress.setNumber(this.desiredProgress.floatValue());
                return true;
            }
            float progress = (float) (time - this.nanotimeStarted.longValue()) / (float) diff;
            this.curProgress.setNumber(
                    Math.lerp(this.lastProgress.floatValue(), this.desiredProgress.floatValue(),
                            progress));
            return true;
        }

        private void setWithoutTimer(float progress) {
            float old = this.desiredProgress.floatValue();
            if (old != progress) {
                this.desiredProgress.setNumber(progress);
            }
        }

        private void setDesired(float progress, long time) {
            long started = System.nanoTime();
            long done = System.nanoTime() + time;
            this.nanotimeStarted.setNumber(started);
            this.nanotimeDone.setNumber(done);
            if (time == 0) {
                this.curProgress.setNumber(progress);
            }
            this.lastProgress.setNumber(this.curProgress.floatValue());
            this.desiredProgress.setNumber(progress);
        }

    }

    /**
     * @author DasBabyPixel
     */
    public static class Scrollbar {

        private final Type type;

        private final GradualProgress progress = new GradualProgress();

        private final NumberValue max = NumberValue.zero();

        private final NumberValue thickness = NumberValue.withValue(17);

        private final BooleanValue visible = BooleanValue.trueValue();

        private final NumberValue display = this.progress.curProgress.max(0).min(this.max);

        public Scrollbar(Type type) {
            this.type = type;
            this.progress.reset();
            this.max.addListener((NumberValue p) -> this.clamp());
            this.progress.desiredProgress.addListener((NumberValue p) -> this.clamp());
        }

        private void clamp() {
            this.progress.setWithoutTimer(Math.clamp(this.progress.desiredProgress.floatValue(), 0,
                    this.max.floatValue()));
        }

        /**
         * @return the progress property
         */
        public NumberValue desiredProgress() {
            return this.progress.desiredProgress;
        }

        /**
         * Requests progress with a time when the animation should be finished
         *
         * @param progress the desired progress
         * @param time     the time when the progress is to be achieved
         */
        public void desireProgress(float progress, long time) {
            this.progress.setDesired(progress, time);
        }

        /**
         * @return the thickness property
         */
        public NumberValue thickness() {
            return this.thickness;
        }

        /**
         * @return the max property
         */
        public NumberValue max() {
            return this.max;
        }

        /**
         * @return the visible property
         */
        public BooleanValue visible() {
            return this.visible;
        }

        /**
         * @return the type
         */
        public Type getType() {
            return this.type;
        }

        /**
         * @author DasBabyPixel
         */
        public enum Type {

            HORIZONTAL {

            }, VERTICAL {

            }
        }
    }

}
