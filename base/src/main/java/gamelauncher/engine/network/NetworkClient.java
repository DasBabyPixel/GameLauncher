/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.network;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.network.packet.Packet;
import gamelauncher.engine.network.packet.PacketHandler;
import gamelauncher.engine.network.packet.PacketRegistry;
import gamelauncher.engine.network.server.NetworkServer;
import gamelauncher.engine.resource.GameResource;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

/**
 * @author DasBabyPixel
 */
public interface NetworkClient extends GameResource {

    /**
     * Starts the {@link NetworkClient}<br>
     * This is basically an init method.<br>
     */
    @Api void start();

    /**
     * Stops the {@link NetworkClient}
     */
    @Api void stop();

    /**
     * @return if the {@link NetworkClient} is running
     */
    @Api boolean running();

    @Api @Nullable ProxyConfiguration proxy();

    @Api void proxy(@Nullable ProxyConfiguration proxy);

    @Api NetworkServer newServer();

    @Api LanDetector createLanDetector(LanDetector.ClientHandler clientHandler);

    /**
     * Connects to the given address
     *
     * @param address
     */
    @Api Connection connect(NetworkAddress address);

    /**
     * @return all current valid connections. Should never contain a connection with state {@link Connection.State#CLOSED} because that is no longer valid
     */
    @Api @UnmodifiableView List<Connection> connections();

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
     * @return the {@link PacketRegistry}
     */
    @Api PacketRegistry packetRegistry();

}
