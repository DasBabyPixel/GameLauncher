package gamelauncher.lwjgl.launcher.gui;

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
import gamelauncher.engine.util.keybind.MouseButtonKeybindEntry;
import gamelauncher.engine.util.keybind.MouseMoveKeybindEntry;
import gamelauncher.engine.util.keybind.ScrollKeybindEntry;
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

//	private Gui currentGui;

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
		this.gui.addListener((ChangeListener<Gui>) (property, oldValue, newValue) -> {
			if (oldValue != null) {
				LWJGLScrollGui.this.guiWidth.unbind();
				LWJGLScrollGui.this.guiHeight.unbind();
				LWJGLScrollGui.this.guiWidth.setNumber(0);
				LWJGLScrollGui.this.guiHeight.setNumber(0);
				oldValue.getXProperty().unbind();
				oldValue.getYProperty().unbind();
				this.GUIs.remove(oldValue);
			}
			if (newValue != null) {
				LWJGLScrollGui.this.guiWidth.bind(newValue.getWidthProperty());
				LWJGLScrollGui.this.guiHeight.bind(newValue.getHeightProperty());
				newValue.getXProperty().bind(LWJGLScrollGui.this.guiX);
				newValue.getYProperty().bind(LWJGLScrollGui.this.guiY);
				this.GUIs.add(newValue);
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
		this.horizontalScrollbar.visible
				.bind(this.displayWidth.mapToBoolean(n -> n.floatValue() < this.guiWidth.floatValue()));
		this.verticalScrollbar.visible
				.bind(this.displayHeight.mapToBoolean(n -> n.floatValue() < this.guiHeight.floatValue()));
		this.guiX.bind(this.displayX.subtract(this.horizontalScrollbar.display));
		this.guiY.bind(
				this.displayY.add(this.displayHeight).subtract(this.guiHeight).add(this.verticalScrollbar.display));

		ScrollbarGui verticalScrollbarGui = new ScrollbarGui(this.getLauncher(), this.guiWidth, this.guiHeight,
				this.verticalScrollbar, this.displayWidth, this.displayHeight);
		verticalScrollbarGui.getXProperty().bind(this.getXProperty().add(this.displayWidth));
		verticalScrollbarGui.getYProperty().bind(this.getYProperty().mapToNumber(n -> {
			if (this.horizontalScrollbar.visible.booleanValue()) {
				return n.doubleValue() + this.horizontalScrollbar.thickness.doubleValue();
			}
			return n;
		}).addDependencies(this.horizontalScrollbar.visible, this.horizontalScrollbar.thickness));
		verticalScrollbarGui.getWidthProperty().bind(this.verticalScrollbar.thickness);
		verticalScrollbarGui.getHeightProperty().bind(this.displayHeight);
		this.GUIs.add(verticalScrollbarGui);

		ScrollbarGui horizontalScrollbarGui = new ScrollbarGui(this.getLauncher(), this.guiWidth, this.guiHeight,
				this.horizontalScrollbar, this.displayWidth, this.displayHeight);
		horizontalScrollbarGui.getXProperty().bind(this.getXProperty());
		horizontalScrollbarGui.getYProperty().bind(this.getYProperty());
		horizontalScrollbarGui.getWidthProperty().bind(this.displayWidth);
		horizontalScrollbarGui.getHeightProperty().bind(this.horizontalScrollbar.thickness);
		this.GUIs.add(horizontalScrollbarGui);
	}

	@Override
	protected final boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick)
			throws GameException {
		final Gui cgui = this.gui.getValue();
		if (cgui != null) {
			ScissorStack scissor = framebuffer.scissorStack();
			scissor.pushScissor(this.displayX, this.displayY, this.displayWidth, this.displayHeight);
			cgui.render(framebuffer, mouseX, mouseY, partialTick);
			scissor.popScissor();
		}

		this.doForGUIs(gui -> {
			gui.render(framebuffer, mouseX, mouseY, partialTick);
		}, gui -> gui != cgui);
		return false;
	}

	@Override
	protected boolean doHandle(KeybindEntry entry) throws GameException {
		if (entry instanceof ScrollKeybindEntry s) {
			float mulx = this.displayWidth.floatValue() / 5;
			float muly = this.displayHeight.floatValue() / 5;
			float dx = s.deltaX();
			float dy = s.deltaY();
			this.horizontalScrollbar.desireProgress(this.horizontalScrollbar.desiredProgress().floatValue() - dx * mulx,
					TimeUnit.MILLISECONDS.toNanos(150));
			this.verticalScrollbar.desireProgress(this.verticalScrollbar.desiredProgress().floatValue() - dy * muly,
					TimeUnit.MILLISECONDS.toNanos(150));
			this.redraw();
			return false;
		}
		return super.doHandle(entry);
	}

	@Override
	protected void doCleanup(Framebuffer framebuffer) throws GameException {
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

		private final NumberValue guiWidth;

		private final NumberValue guiHeight;

		private final NumberValue displayWidth;

		private final NumberValue displayHeight;

		private final BooleanValue dragging;

		private final BooleanValue highlightOrDrag;

		private float dragOffset = 0;

		private void drag(MouseMoveKeybindEntry entry) {
			float newscrolled;
			if (this.scrollbar.type == Scrollbar.Type.VERTICAL) {
				float minsy = this.backgroundY.floatValue() + this.scrollbarIndent.floatValue();
				float maxsy = minsy + this.maxScrollbarHeight.floatValue() - this.scrollbarHeight.floatValue();
				float newy = Math.clamp(entry.mouseY() - this.dragOffset, minsy, maxsy);
				// Invert because its the vertical scrollbar
				float newlocaly = (this.maxScrollbarHeight.floatValue() - this.scrollbarHeight.floatValue())
						- (newy - minsy);
				float newpercentscrolled = newlocaly
						/ (this.maxScrollbarHeight.floatValue() - this.scrollbarHeight.floatValue());
				newscrolled = newpercentscrolled * (this.guiHeight.floatValue() - this.displayHeight.floatValue());
			} else if (this.scrollbar.type == Scrollbar.Type.HORIZONTAL) {
				float minsx = this.backgroundX.floatValue() + this.scrollbarIndent.floatValue();
				float maxsx = minsx + this.maxScrollbarWidth.floatValue() - this.scrollbarWidth.floatValue();
				float newx = Math.clamp(entry.mouseX() - this.dragOffset, minsx, maxsx);
				float newlocalx = newx - minsx;
				float newpercentscrolled = newlocalx
						/ (this.maxScrollbarWidth.floatValue() - this.scrollbarWidth.floatValue());
				newscrolled = newpercentscrolled * (this.guiWidth.floatValue() - this.displayWidth.floatValue());
			} else {
				return;
			}
			this.scrollbar.desireProgress(newscrolled, 0);
//			scrollbar.progress.setNumber(newscrolled);
//			scrollbar.progress.setNumber(scrollbar.displayProgress.floatValue());
			this.redraw();
		}

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
			this.guiWidth = guiWidth;
			this.guiHeight = guiHeight;
			this.displayWidth = displayWidth;
			this.displayHeight = displayHeight;
			this.scrollbar = scrollbar;
			this.scrollbarIndent = NumberValue.constant(1);
			this.dragging = BooleanValue.falseValue();
			this.backgroundX = this.getXProperty();
			this.backgroundY = this.getYProperty();
			this.backgroundWidth = this.getWidthProperty();
			this.backgroundHeight = this.getHeightProperty();
			this.highlight = BooleanValue.falseValue();
			this.maxScrollbarWidth = this.backgroundWidth.subtract(this.scrollbarIndent.multiply(2)).max(0);
			this.maxScrollbarHeight = this.backgroundHeight.subtract(this.scrollbarIndent.multiply(2)).max(0);
			boolean hor = scrollbar.type == Type.HORIZONTAL;
			boolean ver = scrollbar.type == Type.VERTICAL;
			this.scrollbarWidth = ver ? this.maxScrollbarWidth
					: displayWidth.divide(guiWidth).multiply(this.maxScrollbarWidth).min(this.maxScrollbarWidth);
			this.scrollbarHeight = hor ? this.maxScrollbarHeight
					: displayHeight.divide(guiHeight).multiply(this.maxScrollbarHeight).min(this.maxScrollbarHeight);

//			NumberValue progress = scrollbar.displayProgress.divide(scrollbar.max);
//			scrollbar.display.addListener((NumberValue nv) -> {
//				scrollbar.setDesired(scrollbar.display.floatValue(), TimeUnit.MILLISECONDS.toNanos(400));
//			});
			NumberValue progress = scrollbar.display.divide(scrollbar.max);

			if (ver) {
				this.scrollbarX = this.backgroundX.add(this.scrollbarIndent);
				this.scrollbarY = this.backgroundY.add(this.scrollbarIndent)
						.add(this.maxScrollbarHeight.subtract(this.scrollbarHeight)
								.subtract(this.maxScrollbarHeight.subtract(this.scrollbarHeight).multiply(progress)));
			} else if (hor) {
				this.scrollbarX = this.backgroundX.add(this.scrollbarIndent)
						.add(this.maxScrollbarWidth.subtract(this.scrollbarWidth).multiply(progress));
				this.scrollbarY = this.backgroundY.add(this.scrollbarIndent);
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
			this.GUIs.add(backgroundGui);

			ColorGui scrollbarGui = launcher.getGuiManager().createGui(ColorGui.class);
			scrollbarGui.getXProperty().bind(this.scrollbarX);
			scrollbarGui.getYProperty().bind(this.scrollbarY);
			scrollbarGui.getWidthProperty().bind(this.scrollbarWidth);
			scrollbarGui.getHeightProperty().bind(this.scrollbarHeight);
			this.curScrollbarColor.set(this.scrollbarColor);
			this.guiScrollbarColor = scrollbarGui.getColor();
			this.guiScrollbarColor.bind(this.curScrollbarColor.curcolor);
			this.GUIs.add(scrollbarGui);

			this.highlightOrDrag = this.highlight.or(this.dragging);
			this.highlightOrDrag.addListener(Property::getValue);
			this.highlightOrDrag
					.addListener((Property<? extends Boolean> property, Boolean oldValue, Boolean newValue) -> {
						if (newValue.booleanValue()) {
							this.curBackgroundColor.setDesired(this.highlightBackgroundColor,
									TimeUnit.MILLISECONDS.toNanos(100));
							this.curScrollbarColor.setDesired(this.highlightScrollbarColor,
									TimeUnit.MILLISECONDS.toNanos(100));
						} else {
							this.curBackgroundColor.setDesired(this.backgroundColor,
									TimeUnit.MILLISECONDS.toNanos(300));
							this.curScrollbarColor.setDesired(this.scrollbarColor, TimeUnit.MILLISECONDS.toNanos(300));
						}
						this.redraw();
					});
		}

		@Override
		protected void preRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick)
				throws GameException {
			this.curScrollbarColor.calculateCurrent();
			this.curBackgroundColor.calculateCurrent();
			this.scrollbar.progress.calculateCurrent();
		}

		@Override
		protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick)
				throws GameException {
			if (!this.scrollbar.visible.booleanValue()) {
				return false;
			}
			return super.doRender(framebuffer, mouseX, mouseY, partialTick);
		}

		@Override
		protected void doUpdate() throws GameException {
			if (this.curScrollbarColor.calculateCurrent() || this.curBackgroundColor.calculateCurrent()
					|| this.scrollbar.progress.calculateCurrent()) {
				this.redraw();
			}
		}

		@Override
		protected void doInit(Framebuffer framebuffer) throws GameException {
			this.framebuffers.add(framebuffer);
		}

		@Override
		protected void doCleanup(Framebuffer framebuffer) throws GameException {
			this.framebuffers.remove(framebuffer);
		}

		@Override
		protected boolean doHandle(KeybindEntry entry) throws GameException {
			if (entry instanceof MouseMoveKeybindEntry mm) {
				if (Gui.isHovering(this.scrollbarX.floatValue(), this.scrollbarY.floatValue(),
						this.scrollbarWidth.floatValue(), this.scrollbarHeight.floatValue(), mm.mouseX(),
						mm.mouseY())) {
					if (!this.highlight.booleanValue()) {
						this.highlight.setValue(true);
					}
				} else if (this.highlight.booleanValue()) {
					this.highlight.setValue(false);
				}
				if (this.dragging.booleanValue()) {
					this.drag(mm);
				}
			} else if (entry instanceof MouseButtonKeybindEntry mb) {
				if (mb.type() == MouseButtonKeybindEntry.Type.PRESS) {
					if (this.highlight.booleanValue()) {
						if ((mb.getKeybind().getUniqueId() - LWJGLKeybindManager.MOUSE_ADD) == 0) {
							if (this.scrollbar.type == Scrollbar.Type.VERTICAL) {
								this.dragOffset = mb.mouseY() - this.scrollbarY.floatValue();
								this.dragging.setValue(true);
							} else if (this.scrollbar.type == Scrollbar.Type.HORIZONTAL) {
								this.dragOffset = mb.mouseX() - this.scrollbarX.floatValue();
								this.dragging.setValue(true);
							}
						}
					}
				} else if (mb.type() == MouseButtonKeybindEntry.Type.RELEASE) {
					if (this.dragging.booleanValue()) {
						if ((mb.getKeybind().getUniqueId() - LWJGLKeybindManager.MOUSE_ADD) == 0) {
							this.dragging.setValue(false);
						}
					}
				}
			}
			return super.doHandle(entry);
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

		private void set(float other) {
			this.desiredProgress.setNumber(other);
			this.lastProgress.setNumber(other);
			this.curProgress.setNumber(other);
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
			this.curProgress
					.setNumber(Math.lerp(this.lastProgress.floatValue(), this.desiredProgress.floatValue(), progress));
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

	private static class GradualScrollbarColor {

		private final PropertyVector4f curcolor = new PropertyVector4f();

		private final PropertyVector4f desiredColor = new PropertyVector4f();

		private final PropertyVector4f lastColor = new PropertyVector4f();

		private final NumberValue nanotimeDone = NumberValue.zero();

		private final NumberValue nanotimeStarted = NumberValue.zero();

		private void set(PropertyVector4f other) {
			this.desiredColor.set(other);
			this.lastColor.set(other);
			this.curcolor.set(other);
			this.nanotimeDone.setNumber(System.nanoTime());
			this.nanotimeStarted.setNumber(System.nanoTime());
		}

		private boolean calculateCurrent() {
			long time = System.nanoTime();
			if (this.nanotimeDone.longValue() - time < 0) {
				if (this.curcolor.equals(this.desiredColor)) {
					return false;
				}
				this.curcolor.set(this.desiredColor);
				return true;
			}
			long diff = this.nanotimeDone.longValue() - this.nanotimeStarted.longValue();
			if (diff == 0) {
				if (this.curcolor.equals(this.desiredColor)) {
					return false;
				}
				this.curcolor.set(this.desiredColor);
				return true;
			}
			float progress = (float) (time - this.nanotimeStarted.longValue()) / (float) diff;
			this.curcolor.x
					.setNumber(Math.lerp(this.lastColor.x.doubleValue(), this.desiredColor.x.doubleValue(), progress));
			this.curcolor.y
					.setNumber(Math.lerp(this.lastColor.y.doubleValue(), this.desiredColor.y.doubleValue(), progress));
			this.curcolor.z
					.setNumber(Math.lerp(this.lastColor.z.doubleValue(), this.desiredColor.z.doubleValue(), progress));
			this.curcolor.w
					.setNumber(Math.lerp(this.lastColor.w.doubleValue(), this.desiredColor.w.doubleValue(), progress));
			return true;
		}

		private void setDesired(PropertyVector4f other, long time) {
			long started = System.nanoTime();
			long done = System.nanoTime() + time;
			this.nanotimeStarted.setNumber(started);
			this.nanotimeDone.setNumber(done);
			this.lastColor.set(this.curcolor);
			this.desiredColor.set(other);
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

		/**
		 * @param type
		 */
		public Scrollbar(Type type) {
			this.type = type;
			this.progress.set(0);
			this.max.addListener((NumberValue p) -> this.clamp());
			this.progress.desiredProgress.addListener((NumberValue p) -> this.clamp());
		}

		private void clamp() {
			this.progress
					.setWithoutTimer(Math.clamp(this.progress.desiredProgress.floatValue(), 0, this.max.floatValue()));
		}

		/**
		 * @return the progress property
		 */
		public NumberValue desiredProgress() {
			return this.progress.desiredProgress;
		}

		/**
		 * Requests a progress with a time when the animation should be finished
		 * 
		 * @param progress
		 * @param time
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
		@SuppressWarnings("javadoc")
		public static enum Type {

			HORIZONTAL {

			},
			VERTICAL {

			};

		}

	}

}
