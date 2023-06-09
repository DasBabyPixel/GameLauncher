/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.network;

import de.dasbabypixel.annotations.Api;
import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.network.packet.Packet;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;
import java8.util.concurrent.CompletableFuture;

import java.util.concurrent.TimeUnit;

public interface Connection extends GameResource {

    @Api NetworkAddress localAddress();

    @Api NetworkAddress remoteAddress();

    @Api Property<State> state();

    /**
     * Ensures that a state is reached. This blocks the current thread until the given {@code state} is reached.
     *
     * @param state
     * @throws GameException
     */
    @Api StateEnsurance ensureState(State state) throws GameException;

    /**
     * Sends a packet to the other end of this connection.
     *
     * @param packet
     * @throws IllegalStateException when the state is not {@link State#CONNECTED CONNECTED}
     */
    @Api void sendPacket(Packet packet);

    CompletableFuture<Void> sendPacketAsync(Packet packet);

    enum State {
        ESTABLISHING, CONNECTED, CLOSED
    }

    interface StateEnsurance {
        StateEnsurance timeoutAfter(long time, TimeUnit unit);

        StateEnsurance timeoutHandler(TimeoutHandler timeoutHandler);

        State await();

        interface TimeoutHandler {
            void timeout(State state);
        }
    }
}
