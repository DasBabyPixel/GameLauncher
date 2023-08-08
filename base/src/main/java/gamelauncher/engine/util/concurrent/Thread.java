/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.concurrent;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.util.GameException;

public interface Thread {

    @Api default void ensureOnThread() throws GameException {
        if (java.lang.Thread.currentThread() != this) throw new GameException("Wrong thread!");
    }
}
