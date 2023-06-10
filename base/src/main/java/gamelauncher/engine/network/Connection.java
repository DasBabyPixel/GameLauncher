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
import gamelauncher.engine.network.packet.PacketHandler;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;
import java8.util.concurrent.CompletableFuture;

import java.util.concurrent.TimeUnit;

public interface Connection extends GameResource {

    @Api NetworkAddress localAddress();

    @Api NetworkAddress remoteAddress();

    @Api Property<State> state();

    @Api NetworkClient networkClient();

    /**
     * @param <T>
     * @param packetTpye
     * @param handler
     */
    @Api <T extends Packet> void addHandler(Class<T> packetTpye, PacketHandler<T> handler);

    /**
     * @param <T>
     * @param packetTpye
     * @param handler
     */
    @Api <T extends Packet> void removeHandler(Class<T> packetTpye, PacketHandler<T> handler);

    /**
     * Ensures that a state is reached. This blocks the current thread until the given {@code state} is reached.
     *
     * @param state
     * @throws GameException
     */
    @Api StateEnsurance ensureState(State state) throws GameException;

    /**
     * Sends a packet to the other end of this connection.<br>
     * This works like {@link #sendPacketAsync(Packet)} but doesn't return a future, making it require less memory
     *
     * @param packet
     * @throws IllegalStateException when the state is not {@link State#CONNECTED CONNECTED}
     */
    @Api void sendPacket(Packet packet);

    /**
     * Sends a packet to the other end of this connection.
     *
     * @param packet
     * @return a future for when the packet was sent
     * @throws IllegalStateException when the state is not {@link State#CONNECTED CONNECTED}
     */
    @Api CompletableFuture<Void> sendPacketAsync(Packet packet);

    enum State {
        ESTABLISHING, CONNECTED, CLOSED
    }

    interface StateEnsurance {
        @Api StateEnsurance timeoutAfter(long time, TimeUnit unit);

        @Api StateEnsurance timeoutHandler(TimeoutHandler timeoutHandler);

        @Api State await();

        interface TimeoutHandler {
            @Api void timeout(State state);
        }
    }
}
