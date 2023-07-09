/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import gamelauncher.engine.network.Connection;
import java8.util.concurrent.CompletableFuture;

import java.util.concurrent.TimeUnit;

public class CompletedStateEnsurance implements Connection.StateEnsurance {
    private final Connection.State state;

    public CompletedStateEnsurance(Connection.State state) {
        this.state = state;
    }

    @Override public Connection.StateEnsurance timeoutAfter(long time, TimeUnit unit) {
        return this;
    }

    @Override public Connection.StateEnsurance timeoutHandler(Connection.StateEnsurance.TimeoutHandler timeoutHandler) {
        return this;
    }

    @Override public Connection.State await() {
        return state;
    }

    @Override public CompletableFuture<Connection.State> future() {
        return CompletableFuture.completedFuture(state);
    }
}
