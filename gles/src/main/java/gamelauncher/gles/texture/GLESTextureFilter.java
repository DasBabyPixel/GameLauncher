/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.texture;

import gamelauncher.gles.gl.GLES20;

public class GLESTextureFilter {

    public enum FilterType {
        MINIFICATION, MAGNIFICATION
    }

    public enum Filter {
        NEAREST(GLES20.GL_NEAREST), LINEAR(GLES20.GL_LINEAR);
        private final int gl;

        Filter(int gl) {
            this.gl = gl;
        }

        public int gl() {
            return gl;
        }
    }
}
