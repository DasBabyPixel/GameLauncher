/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl.util;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class LWJGLUtil {

    public static BufferedImage rgbaToImage(final int width, final int height, ByteBuffer rgba) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                byte r = rgba.get((y * width + x) * 4);
                byte g = rgba.get((y * width + x) * 4 + 1);
                byte b = rgba.get((y * width + x) * 4 + 2);
                byte a = rgba.get((y * width + x) * 4 + 3);
                image.setRGB(x, y, a << 24 | r << 16 | g << 8 | b);
            }
        }
        return image;
    }

}
