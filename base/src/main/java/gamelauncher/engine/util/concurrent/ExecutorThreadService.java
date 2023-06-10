/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.concurrent;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.util.function.GameRunnable;
import java8.util.concurrent.CompletableFuture;

import java.util.Collection;
import java.util.concurrent.Executor;

/**
 * @author DasBabyPixel
 */
@Api
public interface ExecutorThreadService extends ExecutorThread {

    /**
     * @return the exitfuture
     */
    @Api CompletableFuture<Void> exit();

    @Api int threadCount();

    /**
     * @return the cancelled runnables
     */
    @Api Collection<GameRunnable> exitNow();

    @Api Executor executor();

    /**
     * @return the exitfuture
     */
    @Api CompletableFuture<Void> exitFuture();

}
