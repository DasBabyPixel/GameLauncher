package gamelauncher.engine.gui.guis;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.gui.ParentableAbstractGui;
import org.joml.Math;

/**
 * @author DasBabyPixel
 */
@Api
public class GuiContainer extends ParentableAbstractGui {

    public GuiContainer(GameLauncher launcher) {
        super(launcher);
    }

    @Api public void addGui(Gui gui) {
        this.GUIs.add(gui);
        this.recalculateSize();
    }

    @Api public void removeGui(Gui gui) {
        this.GUIs.remove(gui);
        this.recalculateSize();
    }

    public void recalculateSize() {
        if (!this.widthProperty().bound()) {
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
        if (!this.heightProperty().bound()) {
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
