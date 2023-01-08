package gamelauncher.engine.gui.launcher;

import gamelauncher.engine.gui.LauncherBasedGui;
import gamelauncher.engine.util.property.PropertyVector4f;

/**
 * @author DasBabyPixel
 */
public interface ColorGui extends LauncherBasedGui {

	/**
	 * Change these values to change the color
	 *
	 * @return the color property
	 */
	PropertyVector4f getColor();

}
