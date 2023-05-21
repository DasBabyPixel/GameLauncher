/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.gui.guis;

import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.render.texture.Texture;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.property.PropertyVector4f;

/**
 * @author DasBabyPixel
 */
public interface TextureGui extends Gui {

    /**
     * @return the texture
     */
    Texture texture() throws GameException;

    PropertyVector4f color();

    void texture(Texture texture) throws GameException;

    Property<Texture> textureProperty();

}
