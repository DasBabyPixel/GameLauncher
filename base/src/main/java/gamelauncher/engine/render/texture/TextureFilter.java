/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.render.texture;

import de.dasbabypixel.annotations.Api;

@Api
public interface TextureFilter {
    @Api
    enum FilterType {
        MINIFICATION, MAGNIFICATION
    }

    @Api
    enum Filter {
        NEAREST, LINEAR
    }
}
