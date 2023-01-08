package gamelauncher.engine.gui.launcher;

import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.gui.LauncherBasedGui;

/**
 * @author DasBabyPixel
 */
public interface ScrollGui extends LauncherBasedGui {

	/**
	 * @return the gui
	 */
	Property<Gui> gui();

}
