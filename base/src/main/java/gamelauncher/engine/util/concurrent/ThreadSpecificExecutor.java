/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

public interface ThreadSpecificExecutor extends Executor {

    Thread thread();

    @Override default void execute(@NotNull Runnable command) {
        post(command);
    }

    /**
     * @param runnable
     * @return false if posting failed
     */
    boolean post(Runnable runnable);

    default boolean isCurrentThread() {
        return thread() == Thread.currentThread();
    }
}
