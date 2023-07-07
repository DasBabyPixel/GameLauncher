/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty.standalone.handler;

import gamelauncher.engine.network.Connection;
import gamelauncher.netty.standalone.StandaloneServer;
import gamelauncher.netty.standalone.packet.c2s.PacketConnectToServer;
import gamelauncher.netty.standalone.packet.s2c.PacketClientConnected;
import org.jetbrains.annotations.NotNull;

public class HandlerConnectToServer extends ServerHandler<PacketConnectToServer> {
    public HandlerConnectToServer(StandaloneServer server) {
        super(server);
    }

    @Override public void receivePacket(@NotNull Connection connection, @NotNull PacketConnectToServer packet) {
        String serverId = connection.storedValue(KEY_SERVER);
        if (serverId == null) {
            StandaloneServer.Server s = servers.get(packet.id);
            if (s != null) {
                connection.storeValue(KEY_SERVER_ID, s.idCounter.incrementAndGet());
                connection.storeValue(KEY_SERVER, packet.id);
                s.clients.put(connection.<Integer>storedValue(KEY_SERVER_ID), connection);
                s.owner.sendPacket(new PacketClientConnected(connection.<Integer>storedValue(KEY_SERVER_ID)));
                logger.infof("%s: Connected to Server: %s", connection.remoteAddress(), packet.id);
                connection.sendPacket(new PacketConnectToServer.Response(PacketConnectToServer.Response.SUCCESS));
                return;
            }
        }
        connection.sendPacket(new PacketConnectToServer.Response(PacketConnectToServer.Response.FAILURE));
    }
}
