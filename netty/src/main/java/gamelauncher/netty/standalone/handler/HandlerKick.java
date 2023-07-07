/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty.standalone.handler;

import gamelauncher.engine.network.Connection;
import gamelauncher.netty.standalone.StandaloneServer;
import gamelauncher.netty.standalone.packet.c2s.PacketKick;
import gamelauncher.netty.standalone.packet.s2c.PacketKicked;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class HandlerKick extends ServerHandler<PacketKick> {
    public HandlerKick(StandaloneServer server) {
        super(server);
    }

    @Override public void receivePacket(@NotNull Connection connection, @NotNull PacketKick packet) {
        Collection<String> ids = connection.storedValue(KEY_IDS);
        if (ids == null) return;
        if (!ids.contains(packet.server)) return;
        StandaloneServer.Server server = connection.storedValue(KEY_SERVER);
        Connection target = server.clients.remove(packet.target);
        if (target == null) return;
        target.sendPacket(new PacketKicked("Kicked by owner"));
    }
}
