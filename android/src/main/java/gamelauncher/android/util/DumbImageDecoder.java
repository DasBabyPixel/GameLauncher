/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.util;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.image.Icon;
import gamelauncher.engine.util.image.ImageDecoder;
import java8.util.concurrent.CompletableFuture;

public class DumbImageDecoder extends AbstractGameResource implements ImageDecoder {
    @Override public Icon decodeIcon(ResourceStream resourceStream) throws GameException {
        return null;
    }

    @Override protected CompletableFuture<Void> cleanup0() throws GameException {
        return null;
    }
}
