/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl.util.image;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.image.Icon;
import java8.util.concurrent.CompletableFuture;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class AWTIcon extends AbstractGameResource implements Icon {
    private final List<BufferedImage> images;

    public AWTIcon() {
        this.images = new ArrayList<>();
    }

    public List<BufferedImage> images() {
        return images;
    }

    @Override protected CompletableFuture<Void> cleanup0() throws GameException {
        return null;
    }
}
