/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.concurrent;

/**
 * @author DasBabyPixel
 */
public interface ParkableThread {

    /**
     * Parks this thread
     */
    void park();

    /**
     * Parks this thread for a specified amount of nanoseconds
     */
    void park(long nanos);

    /**
     * Unparks this thread
     */
    void unpark();

    /**
     * @return the name of this thread
     */
    String name();

}
