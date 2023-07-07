/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty.standalone.handler;

import gamelauncher.engine.network.Connection;
import gamelauncher.netty.standalone.StandaloneServer;
import gamelauncher.netty.standalone.packet.c2s.PacketShutdownServer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class HandlerShutdownServer extends ServerHandler<PacketShutdownServer> {
    public HandlerShutdownServer(StandaloneServer server) {
        super(server);
    }

    @Override public void receivePacket(@NotNull Connection connection, @NotNull PacketShutdownServer packet) {
        synchronized (servers) {
            Collection<String> ids = connection.storedValue(KEY_IDS);
            if (ids == null) return;
            if (!ids.contains(packet.id)) return;
            ids.remove(packet.id);
            StandaloneServer.Server s = servers.remove(packet.id);
            StandaloneServer.shutdown(s);
        }
    }
}
