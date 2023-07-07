/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty.standalone.handler;

import gamelauncher.engine.network.Connection;
import gamelauncher.netty.standalone.StandaloneServer;
import gamelauncher.netty.standalone.packet.c2s.PacketPayloadOutC2S;
import gamelauncher.netty.standalone.packet.s2c.PacketPayloadInC2S;
import org.jetbrains.annotations.NotNull;

public class HandlerPayloadOutC2S extends ServerHandler<PacketPayloadOutC2S> {
    public HandlerPayloadOutC2S(StandaloneServer server) {
        super(server);
    }

    @Override public void receivePacket(@NotNull Connection connection, @NotNull PacketPayloadOutC2S packet) {
        String serverId = connection.storedValue(KEY_SERVER);
        if (serverId == null) return;
        synchronized (servers) {
            StandaloneServer.Server s = servers.get(serverId);
            if (s == null) return;
            s.owner.sendPacket(new PacketPayloadInC2S(connection.<Integer>storedValue(KEY_SERVER_ID), packet.data));
        }
    }
}
