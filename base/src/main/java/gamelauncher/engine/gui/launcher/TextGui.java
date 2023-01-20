package gamelauncher.engine.gui.launcher;

import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.gui.LauncherBasedGui;
import gamelauncher.engine.util.component.Component;

public interface TextGui extends LauncherBasedGui {

	Property<Component> text();

}
