/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.concurrent;

public interface ExecutorThreadHelper {
    /**
     * @return the current {@link ExecutorThread}, null if this is not an {@link ExecutorThread}.<br>
     * This should also return a value for threads like the android GLThread
     */
    ExecutorThread currentThread();

}
