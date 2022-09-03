package gamelauncher.lwjgl.launcher.gui;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.ChangeListener;
import de.dasbabypixel.api.property.NumberValue;
import de.dasbabypixel.api.property.Property;
import de.dasbabypixel.api.property.implementation.ObjectProperty;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.launcher.gui.ColorGui;
import gamelauncher.engine.launcher.gui.ScrollGui;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.ScissorStack;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.engine.util.keybind.KeyboardKeybindEntry;
import gamelauncher.engine.util.keybind.MouseMoveKeybindEntry;
import gamelauncher.engine.util.math.Math;
import gamelauncher.engine.util.property.PropertyVector4f;
import gamelauncher.lwjgl.launcher.gui.LWJGLScrollGui.Scrollbar.Type;
import gamelauncher.lwjgl.util.keybind.LWJGLKeybindManager;

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

	private Gui currentGui;

	/**
	 * @param launcher
	 * @throws GameException
	 */
	public LWJGLScrollGui(GameLauncher launcher) throws GameException {
		super(launcher);
		this.gui = ObjectProperty.empty();
		this.guiWidth = NumberValue.zero();
		this.guiHeight = NumberValue.zero();
		this.guiX = NumberValue.zero();
		this.guiY = NumberValue.zero();
		this.gui.addListener(new ChangeListener<Gui>() {

			@Override
			public void handleChange(Property<? extends Gui> property, Gui oldValue, Gui newValue) {
				if (oldValue != null) {
					guiWidth.unbind();
					guiHeight.unbind();
					guiWidth.setNumber(0);
					guiHeight.setNumber(0);
					oldValue.getXProperty().unbind();
					oldValue.getYProperty().unbind();
				}
				if (newValue != null) {
					guiWidth.bind(newValue.getWidthProperty());
					guiHeight.bind(newValue.getHeightProperty());
					newValue.getXProperty().bind(guiX);
					newValue.getYProperty().bind(guiY);
				}
			}

		});
		this.verticalScrollbar = new Scrollbar(Scrollbar.Type.VERTICAL);
		this.horizontalScrollbar = new Scrollbar(Scrollbar.Type.HORIZONTAL);
		this.displayWidth = this.getWidthProperty().mapToNumber(n -> {
			if (this.verticalScrollbar.visible.booleanValue()) {
				return n.floatValue() - this.verticalScrollbar.thickness.floatValue();
			}
			return n.floatValue();
		}).addDependencies(this.verticalScrollbar.visible, this.verticalScrollbar.thickness);
		this.displayHeight = this.getHeightProperty().mapToNumber(n -> {
			if (this.horizontalScrollbar.visible.booleanValue()) {
				return n.floatValue() - this.horizontalScrollbar.thickness.floatValue();
			}
			return n.floatValue();
		}).addDependencies(this.horizontalScrollbar.visible, this.horizontalScrollbar.thickness);
		this.horizontalScrollbar.max.bind(this.guiWidth.subtract(this.displayWidth).max(0));
		this.verticalScrollbar.max.bind(this.guiHeight.subtract(this.displayHeight).max(0));
		this.displayX = this.getXProperty();
		this.displayY = this.getYProperty().mapToNumber(n -> {
			if (this.horizontalScrollbar.visible.booleanValue()) {
				return n.doubleValue() + this.horizontalScrollbar.thickness.doubleValue();
			}
			return n;
		}).addDependencies(this.horizontalScrollbar.visible, this.horizontalScrollbar.thickness);
		this.guiX.bind(this.displayX.subtract(this.horizontalScrollbar.progress.min(this.horizontalScrollbar.max)));
		this.guiY.bind(this.displayY.add(this.displayHeight)
				.subtract(this.guiHeight)
				.add(this.verticalScrollbar.progress.min(this.verticalScrollbar.max)));

		ScrollbarGui verticalScrollbarGui = new ScrollbarGui(getLauncher(), guiWidth, guiHeight, this.verticalScrollbar,
				this.displayWidth, this.displayHeight);
		verticalScrollbarGui.getXProperty().bind(getXProperty().add(displayWidth));
		verticalScrollbarGui.getYProperty().bind(getYProperty().mapToNumber(n -> {
			if (this.horizontalScrollbar.visible.booleanValue()) {
				return n.doubleValue() + this.horizontalScrollbar.thickness.doubleValue();
			}
			return n;
		}).addDependencies(this.horizontalScrollbar.visible, this.horizontalScrollbar.thickness));
		verticalScrollbarGui.getWidthProperty().bind(verticalScrollbar.thickness);
		verticalScrollbarGui.getHeightProperty().bind(displayHeight);
		GUIs.add(verticalScrollbarGui);

		ScrollbarGui horizontalScrollbarGui = new ScrollbarGui(getLauncher(), guiWidth, guiHeight,
				this.horizontalScrollbar, this.displayWidth, this.displayHeight);
		horizontalScrollbarGui.getXProperty().bind(getXProperty());
		horizontalScrollbarGui.getYProperty().bind(getYProperty());
		horizontalScrollbarGui.getWidthProperty().bind(displayWidth);
		horizontalScrollbarGui.getHeightProperty().bind(horizontalScrollbar.thickness);
		GUIs.add(horizontalScrollbarGui);
	}

	@Override
	protected final boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick)
			throws GameException {
		Gui cgui = this.gui.getValue();
		if (cgui != currentGui) {
			if (cgui != null) {
				cgui.cleanup(framebuffer);
			}
			currentGui = cgui;
			if (cgui != null) {
				cgui.init(framebuffer);
			}
		}
		if (cgui != null) {
			ScissorStack scissor = framebuffer.scissorStack();
			scissor.pushScissor(displayX, displayY, displayWidth, displayHeight);
			cgui.render(framebuffer, mouseX, mouseY, partialTick);
			scissor.popScissor();
		}

		doForGUIs(gui -> {
			gui.render(framebuffer, mouseX, mouseY, partialTick);
		});
		return false;
	}

	@Override
	protected boolean doHandle(KeybindEntry entry) throws GameException {
		if (entry instanceof KeyboardKeybindEntry) {
			KeyboardKeybindEntry kb = (KeyboardKeybindEntry) entry;
			if (kb.type() == KeyboardKeybindEntry.Type.HOLD) {
				int id = kb.getKeybind().getUniqueId();
				int glfw = id - LWJGLKeybindManager.KEYBOARD_ADD;
				if (glfw == GLFW_KEY_LEFT) {
					System.out.println("left");
				} else if (glfw == GLFW_KEY_RIGHT) {
					System.out.println("right");
				} else if (glfw == GLFW_KEY_UP) {
					System.out.println("up");
				} else if (glfw == GLFW_KEY_DOWN) {
					System.out.println("down");
				}

			}
		}
		return super.doHandle(entry);
	}

	@Override
	protected void doCleanup(Framebuffer framebuffer) throws GameException {
		if (currentGui != null) {
			currentGui.cleanup(framebuffer);
			currentGui = null;
		}
	}

	/**
	 * @return the gui inside this scrollgui
	 */
	@Override
	public Property<Gui> gui() {
		return gui;
	}

	/**
	 * @author DasBabyPixel
	 */
	public static class ScrollbarGui extends ParentableAbstractGui {

		private final Scrollbar scrollbar;

		private final NumberValue scrollbarIndent;

		private final NumberValue backgroundX;

		private final NumberValue backgroundY;

		private final NumberValue backgroundWidth;

		private final NumberValue backgroundHeight;

		private final NumberValue maxScrollbarWidth;

		private final NumberValue maxScrollbarHeight;

		private final NumberValue scrollbarWidth;

		private final NumberValue scrollbarHeight;

		private final NumberValue scrollbarX;

		private final NumberValue scrollbarY;

		private final BooleanValue highlight;

		private final PropertyVector4f guiBackgroundColor;

		private final PropertyVector4f guiScrollbarColor;

		private final PropertyVector4f backgroundColor = new PropertyVector4f(0.05F, 0.05F, 0.05F, 1F);

		private final PropertyVector4f scrollbarColor = new PropertyVector4f(0.3F, 0.3F, 0.3F, 1F);

		private final PropertyVector4f highlightBackgroundColor = new PropertyVector4f(0F, 0F, 0F, 1F);

		private final PropertyVector4f highlightScrollbarColor = new PropertyVector4f(0.5F, 0.5F, 0.5F, 1F);

		private final Collection<Framebuffer> framebuffers = new ArrayList<>(1);

		private final GradualScrollbarColor curBackgroundColor = new GradualScrollbarColor();

		private final GradualScrollbarColor curScrollbarColor = new GradualScrollbarColor();

		/**
		 * @param launcher
		 * @param gui
		 * @param scrollbar
		 * @param displayWidth
		 * @param displayHeight
		 * @throws GameException
		 */
		private ScrollbarGui(GameLauncher launcher, NumberValue guiWidth, NumberValue guiHeight, Scrollbar scrollbar,
				NumberValue displayWidth, NumberValue displayHeight) throws GameException {
			super(launcher);
			this.scrollbar = scrollbar;
			this.scrollbarIndent = NumberValue.constant(1);
			this.backgroundX = this.getXProperty();
			this.backgroundY = this.getYProperty();
			this.backgroundWidth = this.getWidthProperty();
			this.backgroundHeight = this.getHeightProperty();
			this.highlight = BooleanValue.falseValue();
			this.maxScrollbarWidth = this.backgroundWidth.subtract(scrollbarIndent.multiply(2)).max(0);
			this.maxScrollbarHeight = this.backgroundHeight.subtract(scrollbarIndent.multiply(2)).max(0);
			boolean hor = scrollbar.type == Type.HORIZONTAL;
			boolean ver = scrollbar.type == Type.VERTICAL;
			this.scrollbarWidth = ver ? maxScrollbarWidth
					: displayWidth.divide(guiWidth).multiply(maxScrollbarWidth).min(maxScrollbarWidth);
			this.scrollbarHeight = hor ? maxScrollbarHeight
					: displayHeight.divide(guiHeight).multiply(maxScrollbarHeight).min(maxScrollbarHeight);

			NumberValue progress = scrollbar.progress.min(scrollbar.max).divide(scrollbar.max);

			if (ver) {
				this.scrollbarX = backgroundX.add(scrollbarIndent);
				this.scrollbarY = backgroundY.add(scrollbarIndent)
						.add(this.maxScrollbarHeight.subtract(this.scrollbarHeight)
								.subtract(this.maxScrollbarHeight.subtract(this.scrollbarHeight).multiply(progress)));
			} else if (hor) {
				this.scrollbarX = backgroundX.add(scrollbarIndent)
						.add(this.maxScrollbarWidth.subtract(this.scrollbarWidth).multiply(progress));
				this.scrollbarY = backgroundY.add(scrollbarIndent);
			} else {
				throw new IllegalArgumentException();
			}
			ColorGui backgroundGui = launcher.getGuiManager().createGui(ColorGui.class);
			backgroundGui.getXProperty().bind(this.backgroundX);
			backgroundGui.getYProperty().bind(this.backgroundY);
			backgroundGui.getWidthProperty().bind(this.backgroundWidth);
			backgroundGui.getHeightProperty().bind(this.backgroundHeight);
			this.curBackgroundColor.set(this.backgroundColor);
			this.guiBackgroundColor = backgroundGui.getColor();
			this.guiBackgroundColor.bind(this.curBackgroundColor.curcolor);
			GUIs.add(backgroundGui);

			ColorGui scrollbarGui = launcher.getGuiManager().createGui(ColorGui.class);
			scrollbarGui.getXProperty().bind(this.scrollbarX);
			scrollbarGui.getYProperty().bind(this.scrollbarY);
			scrollbarGui.getWidthProperty().bind(this.scrollbarWidth);
			scrollbarGui.getHeightProperty().bind(this.scrollbarHeight);
			this.curScrollbarColor.set(scrollbarColor);
			this.guiScrollbarColor = scrollbarGui.getColor();
			this.guiScrollbarColor.bind(this.curScrollbarColor.curcolor);
			GUIs.add(scrollbarGui);

			this.highlight.addListener(new ChangeListener<Boolean>() {

				@Override
				public void handleChange(Property<? extends Boolean> property, Boolean oldValue, Boolean newValue) {
					if (newValue.booleanValue()) {
						curBackgroundColor.setDesired(highlightBackgroundColor, TimeUnit.MILLISECONDS.toNanos(100));
						curScrollbarColor.setDesired(highlightScrollbarColor, TimeUnit.MILLISECONDS.toNanos(100));
					} else {
						curBackgroundColor.setDesired(backgroundColor, TimeUnit.MILLISECONDS.toNanos(300));
						curScrollbarColor.setDesired(scrollbarColor, TimeUnit.MILLISECONDS.toNanos(300));
					}
					redraw();
				}

			});
		}

		@Override
		protected void preRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick)
				throws GameException {
			curScrollbarColor.calculateCurrent();
			curBackgroundColor.calculateCurrent();
		}

		@Override
		protected void doUpdate() throws GameException {
			if (curScrollbarColor.calculateCurrent() || curBackgroundColor.calculateCurrent()) {
				redraw();
			}
		}

		@Override
		protected void doInit(Framebuffer framebuffer) throws GameException {
			framebuffers.add(framebuffer);
		}

		@Override
		protected void doCleanup(Framebuffer framebuffer) throws GameException {
			framebuffers.remove(framebuffer);
		}

		@Override
		protected boolean doHandle(KeybindEntry entry) throws GameException {
			if (entry instanceof MouseMoveKeybindEntry) {
				MouseMoveKeybindEntry mm = (MouseMoveKeybindEntry) entry;
				if (Gui.isHovering(scrollbarX.floatValue(), scrollbarY.floatValue(), scrollbarWidth.floatValue(),
						scrollbarHeight.floatValue(), mm.mouseX(), mm.mouseY())) {
					if (!this.highlight.booleanValue()) {
						this.highlight.setValue(true);
					}
				} else {
					if (this.highlight.booleanValue()) {
						this.highlight.setValue(false);
					}
				}
			}
			return super.doHandle(entry);
		}

		/**
		 * @return the scrollbar for this gui
		 */
		public Scrollbar getScrollbar() {
			return scrollbar;
		}

	}

	private static class GradualScrollbarColor {

		private final PropertyVector4f lastrendered = new PropertyVector4f();

		private final PropertyVector4f curcolor = new PropertyVector4f();

		private final PropertyVector4f desiredColor = new PropertyVector4f();

		private final PropertyVector4f lastColor = new PropertyVector4f();

		private final NumberValue nanotimeDone = NumberValue.zero();

		private final NumberValue nanotimeStarted = NumberValue.zero();

		private void set(PropertyVector4f other) {
			lastrendered.set(other);
			desiredColor.set(other);
			lastColor.set(other);
			curcolor.set(other);
			nanotimeDone.setNumber(System.nanoTime());
			nanotimeStarted.setNumber(System.nanoTime());
		}

		private boolean calculateCurrent() {
			long time = System.nanoTime();
			if (nanotimeDone.longValue() - time < 0) {
				if (curcolor.equals(desiredColor)) {
					return false;
				}
				curcolor.set(desiredColor);
				return true;
			}
			long diff = nanotimeDone.longValue() - nanotimeStarted.longValue();
			if (diff == 0) {
				if (curcolor.equals(desiredColor)) {
					return false;
				}
				curcolor.set(desiredColor);
				return true;
			}
			float progress = (float) (time - nanotimeStarted.longValue()) / (float) diff;
			curcolor.x.setNumber(Math.lerp(lastColor.x.doubleValue(), desiredColor.x.doubleValue(), progress));
			curcolor.y.setNumber(Math.lerp(lastColor.y.doubleValue(), desiredColor.y.doubleValue(), progress));
			curcolor.z.setNumber(Math.lerp(lastColor.z.doubleValue(), desiredColor.z.doubleValue(), progress));
			curcolor.w.setNumber(Math.lerp(lastColor.w.doubleValue(), desiredColor.w.doubleValue(), progress));
			return true;
		}

		private void setDesired(PropertyVector4f other, long time) {
			long started = System.nanoTime();
			long done = System.nanoTime() + time;
			nanotimeStarted.setNumber(started);
			nanotimeDone.setNumber(done);
			lastColor.set(curcolor);
			desiredColor.set(other);
		}

	}

	/**
	 * @author DasBabyPixel
	 */
	public static class Scrollbar {

		private final Type type;

		private final NumberValue progress = NumberValue.withValue(50);

		private final NumberValue max = NumberValue.zero();

		private final NumberValue thickness = NumberValue.withValue(17);

		private final BooleanValue visible = BooleanValue.trueValue();

		/**
		 * @param type
		 */
		public Scrollbar(Type type) {
			this.type = type;
		}

		/**
		 * @return the progress property
		 */
		public NumberValue progress() {
			return progress;
		}

		/**
		 * @return the thickness property
		 */
		public NumberValue thickness() {
			return thickness;
		}

		/**
		 * @return the max property
		 */
		public NumberValue max() {
			return max;
		}

		/**
		 * @return the visible property
		 */
		public BooleanValue visible() {
			return visible;
		}

		/**
		 * @return the type
		 */
		public Type getType() {
			return type;
		}

		/**
		 * @author DasBabyPixel
		 */
		@SuppressWarnings("javadoc")
		public static enum Type {

			HORIZONTAL {

			},
			VERTICAL {

			};

		}

	}

}
