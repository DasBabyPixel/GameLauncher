/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.gui.guis;

import de.dasbabypixel.annotations.Api;
import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.gui.Gui;

public interface LineGui extends Gui {
    @Api NumberValue fromX();

    @Api NumberValue fromY();

    @Api NumberValue toX();

    @Api NumberValue toY();

    @Api NumberValue lineWidth();
}
