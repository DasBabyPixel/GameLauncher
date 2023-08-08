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
public interface ExecutorThread extends Thread {

    @Api CompletableFuture<Void> submit(GameRunnable runnable);

    @Api default <T> CompletableFuture<T> submit(GameCallable<T> callable) {
        FuturisticGameRunnable<T> fut = callable.toRunnable();
        submit(fut);
        return fut.getFuture();
    }

    /**
     * Runs all submitted tasks on the current thread.<br>
     * <b>DO NOT CALL THIS UNLESS YOU KNOW WHAT YOU'RE DOING!</b>
     */
    @Api void workQueue();

    /**
     * @return the name of this thread
     */
    String name();

}
