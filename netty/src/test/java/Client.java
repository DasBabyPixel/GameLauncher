/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

import gamelauncher.engine.network.Connection;
import gamelauncher.engine.network.NetworkAddress;
import gamelauncher.engine.network.packet.packets.PacketIdPacket;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.netty.NettyNetworkClient;

import java.util.concurrent.TimeUnit;

public class Client {
    private final NettyNetworkClient client;

    public Client() throws GameException {
        this.client = new NettyNetworkClient();
        Connection con = client.connect(NetworkAddress.localhost(client.port()));
        if (con.ensureState(Connection.State.CONNECTED).timeoutAfter(5, TimeUnit.SECONDS).await() != Connection.State.CONNECTED) {
            client.cleanup();
            throw new GameException("Failed to connect: Connection timed out");
        }
        Threads.await(con.sendPacketAsync(new PacketIdPacket(5)));
        Threads.await(con.cleanup());
        client.cleanup();
    }

    public static void main(String[] args) throws GameException {
        try {
            Basics.setup();
            new Client();
            Basics.cleanup();
        } catch (Throwable th) {
            th.printStackTrace();
            Basics.cleanup();
        }
    }
}
