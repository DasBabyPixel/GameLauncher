package gamelauncher.engine.gui.guis;

import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.guis.ScrollGui.Scrollbar.Type;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public class ScrollGui extends ParentableAbstractGui {

	private final Gui gui;

	private final Scrollbar verticalScrollbar;

	private final Scrollbar horizontalScrollbar;

	private final NumberValue displayWidth;

	private final NumberValue displayHeight;

	/**
	 * @param gui
	 */
	public ScrollGui(Gui gui) {
		super(gui.getLauncher());
		this.gui = gui;
		this.verticalScrollbar = new Scrollbar(Scrollbar.Type.VERTICAL);
		this.horizontalScrollbar = new Scrollbar(Scrollbar.Type.HORIZONTAL);
		this.horizontalScrollbar.max.bind(gui.getWidthProperty());
		this.verticalScrollbar.max.bind(gui.getHeightProperty());
		this.displayWidth = this.getWidthProperty().mapToNumber(n -> {
			if (this.verticalScrollbar.visible.booleanValue()) {
				return n.floatValue() - this.verticalScrollbar.thickness.floatValue();
			}
			return n.floatValue();
		}).addDependencies(this.verticalScrollbar.visible, this.verticalScrollbar.thickness).mapToNumber();
		this.displayHeight = this.getHeightProperty().mapToNumber(n -> {
			if (this.horizontalScrollbar.visible.booleanValue()) {
				return n.floatValue() - this.horizontalScrollbar.thickness.floatValue();
			}
			return n.floatValue();
		}).addDependencies(this.horizontalScrollbar.visible, this.horizontalScrollbar.thickness).mapToNumber();
	}

	/**
	 * @return the gui inside this scrollgui
	 */
	public Gui getGui() {
		return gui;
	}

	/**
	 * @author DasBabyPixel
	 */
	public static class ScrollbarGui extends ParentableAbstractGui {

		private final Scrollbar scrollbar;

		private final NumberValue scrollbarIndent;

		private final NumberValue maxScrollbar;

		private final Gui gui;

		/**
		 * @param launcher
		 * @param gui
		 * @param scrollbar
		 */
		public ScrollbarGui(GameLauncher launcher, Gui gui, Scrollbar scrollbar) {
			super(launcher);
			this.gui = gui;
			this.scrollbar = scrollbar;
			this.scrollbarIndent = NumberValue.constant(1);
			this.maxScrollbar = scrollbar.type.getMaxScrollbar(gui).subtract(scrollbarIndent.multiply(2)).max(0);
		}

		@Override
		protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick)
				throws GameException {
			final float maxScrollbar = this.maxScrollbar.floatValue();
			if (scrollbar.type == Type.HORIZONTAL) {
			} else if (scrollbar.type == Type.VERTICAL) {

			} else {
				throw new UnsupportedOperationException();
			}
			return super.doRender(framebuffer, mouseX, mouseY, partialTick);
		}

		/**
		 * @return the scrollbar for this gui
		 */
		public Scrollbar getScrollbar() {
			return scrollbar;
		}

	}

	/**
	 * @author DasBabyPixel
	 */
	public static class Scrollbar {

		private final Type type;

		private final NumberValue progress = NumberValue.zero();

		private final NumberValue max = NumberValue.zero();

		private final NumberValue thickness = NumberValue.constant(17);

		private final BooleanValue visible = BooleanValue.falseValue();

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

				@Override
				protected NumberValue getMaxScrollbar(Gui gui) {
					return gui.getWidthProperty();
				}

			},
			VERTICAL {

				@Override
				protected NumberValue getMaxScrollbar(Gui gui) {
					return gui.getHeightProperty();
				}

			};

			protected abstract NumberValue getMaxScrollbar(Gui gui);

		}

	}

}
