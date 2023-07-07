/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty.standalone.handler;

import gamelauncher.engine.network.packet.Packet;
import gamelauncher.engine.network.packet.PacketHandler;
import gamelauncher.engine.util.Key;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.netty.standalone.StandaloneServer;

import java.util.Map;

public abstract class ServerHandler<T extends Packet> implements PacketHandler<T> {
    public static final Logger logger = StandaloneServer.logger;
    public static final Key KEY_IDS = StandaloneServer.KEY_IDS;
    public static final Key KEY_SERVER = StandaloneServer.KEY_SERVER;
    public static final Key KEY_SERVER_ID = StandaloneServer.KEY_SERVER_ID;
    public static final int PORT = StandaloneServer.PORT;
    public final StandaloneServer server;
    public final Map<String, StandaloneServer.Server> servers;

    public ServerHandler(StandaloneServer server) {
        this.server = server;
        this.servers = server.servers;
    }
}
