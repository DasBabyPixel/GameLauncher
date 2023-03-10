package gamelauncher.engine.gui.launcher;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.gui.LauncherBasedGui;

public interface LineGui extends LauncherBasedGui {
	NumberValue fromX();

	NumberValue fromY();

	NumberValue toX();

	NumberValue toY();
}
