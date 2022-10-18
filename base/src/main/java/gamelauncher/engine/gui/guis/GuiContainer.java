package gamelauncher.engine.gui.guis;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.gui.ParentableAbstractGui;

/**
 * @author DasBabyPixel
 */
public class GuiContainer extends ParentableAbstractGui {

	/**
	 * @param launcher
	 */
	public GuiContainer(GameLauncher launcher) {
		super(launcher);
	}

	/**
	 * @param gui
	 */
	public void addGui(Gui gui) {
		this.GUIs.add(gui);
		this.recalculateSize();
	}

	/**
	 * @param gui
	 */
	public void removeGui(Gui gui) {
		this.GUIs.remove(gui);
		this.recalculateSize();
	}

	private void recalculateSize() {
		if (!this.getWidthProperty().isBound()) {
			float minx = Float.MAX_VALUE;
			float w = 0;
			for (Gui gui : this.GUIs) {
				if (gui.getX() < minx) {
					if (w != 0) {
						float diff = minx - gui.getX();
						w += diff;
						minx -= diff;
					} else {
						minx = gui.getX();
					}
				}
				w = Math.max(w, gui.getX() - minx + gui.getWidth());
			}
			this.setWidth(w);
		}
		if (!this.getHeightProperty().isBound()) {
			float miny = Float.MAX_VALUE;
			float h = 0;
			for (Gui gui : this.GUIs) {
				if (gui.getY() < miny) {
					if (h != 0) {
						float diff = miny - gui.getY();
						h += diff;
						miny -= diff;
					} else {
						miny = gui.getY();
					}
				}
				h = Math.max(h, gui.getY() - miny + gui.getHeight());
			}
			this.setHeight(h);
		}
	}

}
