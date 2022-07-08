package gamelauncher.engine.launcher.gui;

import de.dasbabypixel.api.property.NumberValue;
import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.gui.LauncherBasedGui;

/**
 * @author DasBabyPixel
 */
public interface TextGui extends LauncherBasedGui {

	/**
	 * @return the text {@link Property}
	 */
	Property<String> text();
	
	/**
	 * @return the size {@link Property}
	 */
	NumberValue size();
	
}
