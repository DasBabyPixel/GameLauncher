/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty.standalone.handler;

import gamelauncher.engine.network.Connection;
import gamelauncher.netty.standalone.StandaloneServer;
import gamelauncher.netty.standalone.packet.c2s.PacketPayloadOutS2C;
import gamelauncher.netty.standalone.packet.s2c.PacketPayloadInS2C;
import org.jetbrains.annotations.NotNull;

public class HandlerPayloadOutS2C extends ServerHandler<PacketPayloadOutS2C> {
    public HandlerPayloadOutS2C(StandaloneServer server) {
        super(server);
    }

    @Override public void receivePacket(@NotNull Connection connection, @NotNull PacketPayloadOutS2C packet) {
        synchronized (servers) {
            StandaloneServer.Server s = servers.get(packet.serverId);
            if (s == null) return;
            Connection con = s.clients.get(packet.target);
            if (con == null) return;
            con.sendPacket(new PacketPayloadInS2C(packet.data));
        }
    }
}
