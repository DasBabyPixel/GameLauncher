/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.network.server;

import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.resource.GameResource;
import java8.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;

public interface NetworkServer extends GameResource {

    Property<State> state();

    CompletableFuture<StartResult> start();

    void stop();

    void serverListener(@Nullable ServerListener serverListener);

    @Nullable ServerListener serverListener();

    interface StartResult {
        class Success implements StartResult {
        }

        class Failure implements StartResult {
            private final Throwable cause;

            public Failure(Throwable cause) {
                this.cause = cause;
            }

            public Throwable cause() {
                return cause;
            }
        }
    }

    enum State {
        OFFLINE, STARTING, RUNNING, STOPPING
    }
}
