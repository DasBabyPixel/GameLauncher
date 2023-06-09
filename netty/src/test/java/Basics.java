/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.logging.Logger;

public class Basics {

    public static void setup() {
        Logger.Initializer.init(null);
        Logger.asyncLogStream().start();
    }

    public static void cleanup() throws GameException {
        Threads.await(Logger.asyncLogStream().cleanup());
    }
}
