/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.texture;

import gamelauncher.engine.render.texture.TextureFilter;
import gamelauncher.gles.gl.GLES20;

public class GLESTextureFilter {

    public static int gl(TextureFilter.Filter filter) {
        switch (filter) {
            case LINEAR:
                return GLES20.GL_LINEAR;
            case NEAREST:
                return GLES20.GL_NEAREST;
            default:
                throw new IllegalArgumentException();
        }
    }
}
