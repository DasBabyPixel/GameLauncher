/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import de.dasbabypixel.api.property.InvalidationListener;
import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.network.Connection;
import java8.util.concurrent.CompletableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PropertyStateEnsurance implements Connection.StateEnsurance {
    private final CompletableFuture<Void> f = new CompletableFuture<>();
    private final CompletableFuture<Connection.State> future = new CompletableFuture<>();
    private final Connection.State wantedState;
    private final Property<? extends Connection.State> stateProperty;
    private long time = 5;
    private TimeUnit unit = TimeUnit.SECONDS;
    private TimeoutHandler timeoutHandler;

    public PropertyStateEnsurance(Property<? extends Connection.State> stateProperty, Connection.State wantedState) {
        this.stateProperty = stateProperty;
        this.wantedState = wantedState;
        stateProperty = stateProperty.map(s -> s);
        Property<? extends Connection.State> finalStateProperty = stateProperty;
        InvalidationListener l = p -> {
            if (p.value() == wantedState) {
                f.complete(null);
            }
        };
        stateProperty.addListener(l);
        if (stateProperty.value() == wantedState) {
            stateProperty.removeListener(l);
            f.complete(null);
        }
        f.exceptionally(t -> null).thenRun(() -> finalStateProperty.removeListener(l));
    }

    @Override public Connection.StateEnsurance timeoutAfter(long time, TimeUnit unit) {
        this.time = time;
        this.unit = unit;
        return this;
    }

    @Override public Connection.StateEnsurance timeoutHandler(TimeoutHandler timeoutHandler) {
        this.timeoutHandler = timeoutHandler;
        return this;
    }

    @Override public Connection.State await() {
        try {
            f.get(time, unit);
            future.complete(wantedState);
            return wantedState;
        } catch (InterruptedException | ExecutionException e) {
            future.completeExceptionally(e);
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            future.completeExceptionally(e);
            Connection.State s = stateProperty.value();
            if (s == wantedState) return s;
            if (timeoutHandler != null) timeoutHandler.timeout(s);
            return s;
        }
    }

    @Override public CompletableFuture<Connection.State> future() {
        return future;
    }
}
