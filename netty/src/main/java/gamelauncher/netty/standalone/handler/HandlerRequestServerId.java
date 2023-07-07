/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty.standalone.handler;

import gamelauncher.engine.network.Connection;
import gamelauncher.netty.standalone.StandaloneServer;
import gamelauncher.netty.standalone.packet.c2s.PacketRequestServerId;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class HandlerRequestServerId extends ServerHandler<PacketRequestServerId> {
    public HandlerRequestServerId(StandaloneServer server) {
        super(server);
    }

    @Override public void receivePacket(@NotNull Connection connection, @NotNull PacketRequestServerId packet) {
        synchronized (servers) {
            String id = server.newId();
            servers.put(id, new StandaloneServer.Server(id, connection));
            Collection<String> ids = connection.storedValue(KEY_IDS, ArrayList::new);
            ids.add(id);
            logger.infof("%s: New Server: %s", connection.remoteAddress(), id);
            connection.sendPacket(new PacketRequestServerId.Response(id));
        }
    }
}
