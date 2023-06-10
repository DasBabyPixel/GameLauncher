/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.render.font;

import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;
import java8.util.concurrent.CompletableFuture;

/**
 * @author DasBabyPixel
 */
public interface Font extends GameResource {

    /**
     * @return the data of this font
     * @throws GameException an exception
     */
    byte[] data() throws GameException;

    CompletableFuture<byte[]> dataFuture();

}
