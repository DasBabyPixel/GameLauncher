/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl.util.image;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.image.Icon;
import gamelauncher.engine.util.image.ImageDecoder;
import java8.util.concurrent.CompletableFuture;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.formats.ico.IcoImageParser;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class AWTImageDecoder extends AbstractGameResource implements ImageDecoder {
    @Override public Icon decodeIcon(ResourceStream resourceStream) throws GameException {
        byte[] bytes = resourceStream.readAllBytes();
        resourceStream.cleanup();
        AWTIcon icon = new AWTIcon();
        try {
            List<BufferedImage> images = new IcoImageParser().getAllBufferedImages(bytes);
            if (images.isEmpty()) throw new GameException("No image found");
            icon.images().addAll(images);
        } catch (ImageReadException | IOException e) {
            throw GameException.wrap(e);
        }
        return icon;
    }

    @Override protected CompletableFuture<Void> cleanup0() throws GameException {
        return null;
    }
}
