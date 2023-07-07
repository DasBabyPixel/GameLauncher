/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl;

import gamelauncher.lwjgl.render.LWJGLGLES;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.stb.STBImage;

public class Libraries {

    public static void init() {
        try {
            Class.forName(STBImage.class.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        LWJGLGLES.initialize();
        GLFW.getLibrary();
    }

}
