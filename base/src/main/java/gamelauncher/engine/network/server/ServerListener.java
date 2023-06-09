/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.network.server;

import gamelauncher.engine.network.Connection;

public interface ServerListener {

    default void connected(Connection connection) {

    }

}
