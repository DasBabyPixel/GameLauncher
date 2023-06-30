/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util;

import gamelauncher.engine.util.logging.Logger;

public class Debug {
    public static final boolean debug = Config.DEBUG.value();
    public static final boolean trackResources = Config.TRACK_RESOURCES.value();
    public static final boolean calculateThreadStacks = Config.CALCULATE_THREAD_STACKS.value();
    private static final Logger logger = Logger.logger();

    public static void printInformation() {
        logger.infof("Debug: %s", debug);
        logger.infof("TrackResources: %s", trackResources);
        logger.infof("CalculateThreadStacks: %s", calculateThreadStacks);
    }
}
