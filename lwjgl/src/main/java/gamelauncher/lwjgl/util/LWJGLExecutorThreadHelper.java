/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl.util;

import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.ExecutorThreadHelper;

public class LWJGLExecutorThreadHelper implements ExecutorThreadHelper {
    @Override public ExecutorThread currentThread() {
        if (Thread.currentThread() instanceof ExecutorThread) {
            return (ExecutorThread) Thread.currentThread();
        }
        return null;
    }
}
