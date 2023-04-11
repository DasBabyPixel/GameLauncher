/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.concurrent;

import gamelauncher.engine.util.function.GameCallable;
import gamelauncher.engine.util.function.GameCallable.FuturisticGameRunnable;
import gamelauncher.engine.util.function.GameRunnable;

import java.util.concurrent.CompletableFuture;

/**
 * This is a {@link Thread} with executor capabilities.
 *
 * @author DasBabyPixel
 */
public interface ExecutorThread extends ParkableThread {

    /**
     * @param runnable
     * @return a new future
     */
    default CompletableFuture<Void> submit(GameRunnable runnable) {
        return submitLast(runnable);
    }

    /**
     * @param <T>
     * @param callable
     * @return a new future
     */
    default <T> CompletableFuture<T> submit(GameCallable<T> callable) {
        return submitLast(callable);
    }

    /**
     * @param runnable
     * @return a new future
     */
    CompletableFuture<Void> submitLast(GameRunnable runnable);

    /**
     * @param <T>
     * @param callable
     * @return a new future
     */
    default <T> CompletableFuture<T> submitLast(GameCallable<T> callable) {
        FuturisticGameRunnable<T> fut = callable.toRunnable();
        submitLast(fut);
        return fut.getFuture();
    }

    /**
     * @param runnable
     * @return a new future
     */
    CompletableFuture<Void> submitFirst(GameRunnable runnable);

    /**
     * @param <T>
     * @param callable
     * @return a new future
     */
    default <T> CompletableFuture<T> submitFirst(GameCallable<T> callable) {
        FuturisticGameRunnable<T> fut = callable.toRunnable();
        submitFirst(fut);
        return fut.getFuture();
    }

    /**
     * Parks this thread. This will stop parking once a task is available
     */
    @Override
    void park();

    /**
     * Parks this thread. This will stop parking once a task is available
     */
    @Override
    void park(long nanos);

    /**
     * Unparks this thread
     */
    @Override
    void unpark();

    /**
     * Runs all submitted tasks on the current thread. DO NOT CALL THIS UNLESS YOU
     * KNOW WHAT YOU'RE DOING!
     */
    void workQueue();

}
