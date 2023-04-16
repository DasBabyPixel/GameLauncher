/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.gui.guis;

import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.render.texture.Texture;

/**
 * @author DasBabyPixel
 */
public interface TextureGui extends Gui {

    /**
     * @return the texture
     */
    Texture texture();

}
