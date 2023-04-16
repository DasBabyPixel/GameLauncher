package gamelauncher.engine.gui.launcher;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.gui.Gui;

public interface LineGui extends Gui {
    NumberValue fromX();

    NumberValue fromY();

    NumberValue toX();

    NumberValue toY();
}
