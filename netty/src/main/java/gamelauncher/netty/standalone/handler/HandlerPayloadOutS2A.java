/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty.standalone.handler;

import gamelauncher.engine.network.Connection;
import gamelauncher.netty.standalone.StandaloneServer;
import gamelauncher.netty.standalone.packet.c2s.PacketPayloadOutS2A;
import gamelauncher.netty.standalone.packet.s2c.PacketPayloadInS2C;
import org.jetbrains.annotations.NotNull;

public class HandlerPayloadOutS2A extends ServerHandler<PacketPayloadOutS2A> {
    public HandlerPayloadOutS2A(StandaloneServer server) {
        super(server);
    }

    @Override public void receivePacket(@NotNull Connection connection, @NotNull PacketPayloadOutS2A packet) {
        synchronized (servers) {
            StandaloneServer.Server s = servers.get(packet.serverId);
            if (s == null) return;
            PacketPayloadInS2C payload = new PacketPayloadInS2C(packet.data);
            for (Connection con : s.clients.values()) con.sendPacket(payload);
        }
    }
}
