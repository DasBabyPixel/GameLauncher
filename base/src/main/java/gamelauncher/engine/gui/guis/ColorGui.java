/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.gui.guis;

import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.util.property.PropertyVector4f;

/**
 * @author DasBabyPixel
 */
public interface ColorGui extends Gui {

    /**
     * Change these values to change the color
     *
     * @return the color property
     */
    PropertyVector4f color();

}
