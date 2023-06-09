/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

import gamelauncher.engine.util.GameException;
import gamelauncher.netty.NettyNetworkClient;

public class Server {
    private final NettyNetworkClient client;

    public Server() {
        this.client = new NettyNetworkClient();
        client.start();
        client.server().start();
    }

    public static void main(String[] args) throws GameException {
        Basics.setup();
        new Server();
        Basics.cleanup();
    }
}
