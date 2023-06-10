/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.font.bitmap;

import gamelauncher.engine.render.font.Font;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;
import java8.util.concurrent.CompletableFuture;

public class BasicFont extends AbstractGameResource implements Font {

    private final byte[] data;

    BasicFont(ResourceStream stream) throws GameException {
        data = stream.readAllBytes();
        stream.cleanup();
    }

    @Override public byte[] data() throws GameException {
        return this.data;
    }

    @Override public CompletableFuture<byte[]> dataFuture() {
        return CompletableFuture.completedFuture(data);
    }

    @Override public CompletableFuture<Void> cleanup0() throws GameException {
        return null;
    }
}
