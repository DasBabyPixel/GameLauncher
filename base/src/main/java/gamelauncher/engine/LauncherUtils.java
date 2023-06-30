/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine;

import gamelauncher.engine.util.logging.Logger;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

class LauncherUtils {
    private static FileLock lock;
    private static FileChannel fileChannel;

    /**
     * Aquires the Application Lock and ensures only a single instance is running
     */
    public static void acquire(Logger logger, Path gameDirectory) {
        try {
            fileChannel = FileChannel.open(gameDirectory.resolve("lock"), StandardOpenOption.APPEND, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            lock = fileChannel.tryLock(0, Long.MAX_VALUE, false);
            if (lock == null) {
                fileChannel.close();
                logger.error("GameLauncher already running");
                Logger.asyncLogStream().cleanup();
                System.exit(0);
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    public static void release() throws IOException {
        lock.release();
        fileChannel.close();
    }
}
