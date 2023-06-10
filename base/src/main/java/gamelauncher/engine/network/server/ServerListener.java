/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.network.server;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.network.Connection;

public interface ServerListener {

    @Api default void connected(Connection connection) {
    }

    @Api default void disconnected(Connection connection) {
    }
}
