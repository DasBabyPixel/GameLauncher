/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.resource;

import gamelauncher.engine.util.GameException;
import java8.util.concurrent.CompletableFuture;

public class DummyGameResource extends AbstractGameResource {
    @Override protected CompletableFuture<Void> cleanup0() throws GameException {
        return null;
    }
}
