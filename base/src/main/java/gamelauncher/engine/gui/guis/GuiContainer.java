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
		if (!this.widthProperty().isBound()) {
			float minx = Float.MAX_VALUE;
			float w = 0;
			for (Gui gui : this.GUIs) {
				if (gui.x() < minx) {
					if (w != 0) {
						float diff = minx - gui.x();
						w += diff;
						minx -= diff;
					} else {
						minx = gui.x();
					}
				}
				w = Math.max(w, gui.x() - minx + gui.width());
			}
			this.width(w);
		}
		if (!this.heightProperty().isBound()) {
			float miny = Float.MAX_VALUE;
			float h = 0;
			for (Gui gui : this.GUIs) {
				if (gui.y() < miny) {
					if (h != 0) {
						float diff = miny - gui.y();
						h += diff;
						miny -= diff;
					} else {
						miny = gui.y();
					}
				}
				h = Math.max(h, gui.y() - miny + gui.height());
			}
			this.height(h);
		}
	}
}
