/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

import gamelauncher.engine.network.Connection;
import gamelauncher.engine.network.ProxyConfiguration;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.netty.NettyNetworkClient;
import gamelauncher.netty.standalone.StandaloneServer;
import gamelauncher.netty.standalone.packet.c2s.PacketRequestServerId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class Client {
    private final NettyNetworkClient client;

    public Client() throws GameException, URISyntaxException, MalformedURLException {
        this.client = new NettyNetworkClient();
        client.port(19452);
        client.proxy(new ProxyConfiguration() {
            @Override public @NotNull String hostname() {
                return null;
            }

            @Override public int port() {
                return 0;
            }

            @Override public @Nullable String username() {
                return null;
            }

            @Override public @Nullable String password() {
                return null;
            }
        });
        StandaloneServer.registerPackets(client.packetRegistry());
        Connection con = client.connect(new URI("https", null, "localhost", 19452, "/orbits/", null, null));
//        Connection con = client.connect(new URI("https://ssh.darkcube.eu/orbits/"));
        if (con.ensureState(Connection.State.CONNECTED).timeoutAfter(10, TimeUnit.SECONDS).await() != Connection.State.CONNECTED) {
            client.cleanup();
            throw new GameException("Failed to connect: Connection timed out");
        }
        Threads.await(con.sendPacketAsync(new PacketRequestServerId()));
        //        client.cleanup();
    }

    public static void main(String[] args) throws GameException {
        System.setProperty("debug", "true");
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
