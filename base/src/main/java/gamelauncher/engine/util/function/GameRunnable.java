/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.function;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public interface GameRunnable {

    void run() throws GameException;

    /**
     * Converts this {@link GameRunnable} to a {@link Runnable} object
     *
     * @return a {@link Runnable} for this {@link GameRunnable}
     */
    default Runnable toRunnable() {
        return () -> {
            try {
                run();
            } catch (GameException ex) {
                throw new RuntimeException(ex);
            }
        };
    }

    /**
     * Converts this {@link GameRunnable} to a {@link GameCallable} object
     *
     * @return a {@link GameCallable} for this {@link GameRunnable}
     */
    default GameCallable<Void> toCallable() {
        return () -> {
            run();
            return null;
        };
    }

}
