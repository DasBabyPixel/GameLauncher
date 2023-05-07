/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.concurrent;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.util.function.GameCallable;
import gamelauncher.engine.util.function.GameCallable.FuturisticGameRunnable;
import gamelauncher.engine.util.function.GameRunnable;
import java8.util.concurrent.CompletableFuture;

/**
 * This is a {@link Thread} with executor capabilities.
 *
 * @author DasBabyPixel
 */
@Api
public interface ExecutorThread extends ParkableThread {

    @Api default CompletableFuture<Void> submit(GameRunnable runnable) {
        return submitLast(runnable);
    }

    @Api

    default <T> CompletableFuture<T> submit(GameCallable<T> callable) {
        return submitLast(callable);
    }

    @Api CompletableFuture<Void> submitLast(GameRunnable runnable);

    @Api default <T> CompletableFuture<T> submitLast(GameCallable<T> callable) {
        FuturisticGameRunnable<T> fut = callable.toRunnable();
        submitLast(fut);
        return fut.getFuture();
    }

    @Api CompletableFuture<Void> submitFirst(GameRunnable runnable);

    @Api default <T> CompletableFuture<T> submitFirst(GameCallable<T> callable) {
        FuturisticGameRunnable<T> fut = callable.toRunnable();
        submitFirst(fut);
        return fut.getFuture();
    }

    /**
     * Parks this thread. This will stop parking once a task is available
     */
    @Override void park();

    /**
     * Parks this thread. This will stop parking once a task is available
     */
    @Override void park(long nanos);

    /**
     * Unparks this thread
     */
    @Override void unpark();

    /**
     * Runs all submitted tasks on the current thread. DO NOT CALL THIS UNLESS YOU
     * KNOW WHAT YOU'RE DOING!
     */
    @Api void workQueue();

}
