/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.network;

import de.dasbabypixel.api.property.Property;
import java8.util.concurrent.CompletableFuture;

public interface NetworkServer {

    Property<State> state();

    CompletableFuture<StartResult> start();

    void stop();

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
