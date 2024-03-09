/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl;

import gamelauncher.lwjgl.render.LWJGLGL;
import gamelauncher.lwjgl.render.LWJGLGLES;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.stb.STBImage;

class Libraries {

    public static void init() {
        try {
            Class.forName(STBImage.class.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (LWJGLGameLauncher.USE_GLES.value()) {
            LWJGLGLES.initialize();
        } else {
            LWJGLGL.initialize();
        }
        GLFW.getLibrary();
    }

}
