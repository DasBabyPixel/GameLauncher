/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util;

public class Debug {
    public static final boolean debug = Boolean.getBoolean("debug");
    /**
     * Wheather or not stack traces should be calculated with causes from other threads when tasks
     * are submitted
     */
    public static final boolean trackResources = debug || Boolean.getBoolean("trackResources");
    public static final boolean calculateThreadStacks = debug || Boolean.getBoolean("calculateThreadStacks");
}
