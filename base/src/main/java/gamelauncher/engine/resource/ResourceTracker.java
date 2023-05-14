/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.resource;

import gamelauncher.engine.util.Debug;
import gamelauncher.engine.util.logging.Logger;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceTracker {
    private static final Collection<GameResource> resources = ConcurrentHashMap.newKeySet();
    private static Logger logger;

    /**
     * @param resource a resource
     */
    public static void startTracking(GameResource resource) {
        if (Debug.trackResources) resources.add(resource);
    }

    /**
     * @param resource a resource
     */
    public static void stopTracking(GameResource resource) {
        if (Debug.trackResources) resources.remove(resource);
    }

    private static Logger logger() {
        if (logger == null) {
            return logger = Logger.logger();
        }
        return logger;
    }

    /**
     * Called on exit
     */
    public static void exit() {
        if (Debug.trackResources) {
            for (GameResource resource : resources) {
                if (resource instanceof AbstractGameResource) {
                    AbstractGameResource aresource = (AbstractGameResource) resource;
                    Exception ex = new Exception("Stack: " + aresource.creationThreadName);
                    ex.setStackTrace(aresource.creationStack);
                    logger().errorf("Memory Leak: %s%n%s", resource, ex);
                } else {
                    logger().errorf("Memory Leak: %s", resource);
                }
            }
        }
    }
}
