/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

import gamelauncher.engine.network.server.NetworkServer;
import gamelauncher.engine.util.GameException;
import gamelauncher.netty.NettyNetworkClient;

public class Server {
    private final NettyNetworkClient client;

    public Server() {
        this.client = new NettyNetworkClient();
        client.packetRegistry().register(TestPacket.class, TestPacket::new);
        client.start();
        NetworkServer server = client.newServer();
        server.start();
    }

    public static void main(String[] args) throws GameException {
        Basics.setup();
        new Server();
        Basics.cleanup();
    }
}
