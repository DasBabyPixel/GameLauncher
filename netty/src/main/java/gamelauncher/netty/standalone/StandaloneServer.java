/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty.standalone;

import gamelauncher.engine.network.Connection;
import gamelauncher.engine.network.packet.PacketRegistry;
import gamelauncher.engine.network.server.ServerListener;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.netty.NettyNetworkClient;
import gamelauncher.netty.NettyServer;
import gamelauncher.netty.standalone.handler.*;
import gamelauncher.netty.standalone.packet.c2s.*;
import gamelauncher.netty.standalone.packet.s2c.*;
import org.fusesource.jansi.AnsiConsole;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class StandaloneServer {
    public static final Logger logger = Logger.logger();
    public static final Key KEY_IDS = new Key("ids");
    public static final Key KEY_SERVER = new Key("server");
    public static final Key KEY_SERVER_ID = new Key("server");
    public static final int PORT = Integer.getInteger("gamelauncher.standalone.port", 19452);
    public final Map<String, Server> servers = new HashMap<>();
    public final NettyNetworkClient client;
    public final NettyServer server;

    public StandaloneServer() {
        client = new NettyNetworkClient();
        client.port(PORT);
        registerPackets(client.packetRegistry());
        client.start();
        client.addHandler(PacketRequestServerId.class, new HandlerRequestServerId(this));
        client.addHandler(PacketConnectToServer.class, new HandlerConnectToServer(this));
        client.addHandler(PacketShutdownServer.class, new HandlerShutdownServer(this));
        client.addHandler(PacketPayloadOutC2S.class, new HandlerPayloadOutC2S(this));
        client.addHandler(PacketPayloadOutS2C.class, new HandlerPayloadOutS2C(this));
        client.addHandler(PacketPayloadOutS2A.class, new HandlerPayloadOutS2A(this));
        client.addHandler(PacketKick.class, new HandlerKick(this));
        server = client.newServer();
        server.serverListener(new ServerListener() {
            @Override public void disconnected(Connection connection) {
                synchronized (servers) {
                    Collection<String> ids = connection.storedValue(KEY_IDS);
                    connection.storeValue(KEY_IDS, null);
                    if (ids != null) {
                        for (String id : ids) {
                            Server s = servers.remove(id);
                            shutdown(s);
                        }
                        ids.clear();
                    }
                }
                String serverId = connection.storedValue(KEY_SERVER);
                if (serverId != null) {
                    connection.storeValue(KEY_SERVER, null);
                    Server server = servers.get(serverId);
                    if (server != null) {
                        server.owner.sendPacket(new PacketClientDisconnected(connection.storedValue(KEY_SERVER_ID)));
                        server.clients.remove(connection.<Integer>storedValue(KEY_SERVER_ID));
                    }
                    connection.storeValue(KEY_SERVER_ID, null);
                }
            }
        });
        server.start();
        server.cleanupFuture().thenRun(() -> {
            try {
                cleanup();
            } catch (GameException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void main(String[] args) throws GameException {
        setup();
        new StandaloneServer();
    }

    public static void shutdown(Server server) {
        for (Connection connection : server.clients.values()) {
            connection.storeValue(KEY_SERVER, null);
            connection.sendPacket(new PacketKicked("Server stopped"));
        }
        logger.infof("Server Shutdown: %s", server.id);
        server.clients.clear();
        Collection<String> ids = server.owner.storedValue(KEY_IDS);
        if (ids != null) ids.remove(server.id);
    }

    public static void registerPackets(PacketRegistry registry) {
        registry.register(PacketRequestServerId.class, PacketRequestServerId::new);
        registry.register(PacketRequestServerId.Response.class, PacketRequestServerId.Response::new);
        registry.register(PacketConnectToServer.class, PacketConnectToServer::new);
        registry.register(PacketConnectToServer.Response.class, PacketConnectToServer.Response::new);
        registry.register(PacketKicked.class, PacketKicked::new);
        registry.register(PacketKick.class, PacketKick::new);
        registry.register(PacketClientConnected.class, PacketClientConnected::new);
        registry.register(PacketClientDisconnected.class, PacketClientDisconnected::new);
        registry.register(PacketShutdownServer.class, PacketShutdownServer::new);
        registry.register(PacketPayloadInC2S.class, PacketPayloadInC2S::new);
        registry.register(PacketPayloadInS2C.class, PacketPayloadInS2C::new);
        registry.register(PacketPayloadOutC2S.class, PacketPayloadOutC2S::new);
        registry.register(PacketPayloadOutS2A.class, PacketPayloadOutS2A::new);
        registry.register(PacketPayloadOutS2C.class, PacketPayloadOutS2C::new);
    }

    public static void setup() {
        if (System.console() != null) {
            AnsiConsole.systemInstall();
            Runtime.getRuntime().addShutdownHook(new Thread(AnsiConsole::systemUninstall));
        }
        Logger.Initializer.init(null);
        Logger.asyncLogStream().start();
    }

    public static void cleanup() throws GameException {
        Threads.await(Logger.asyncLogStream().cleanup());
    }

    public String newId() {
        while (true) {
            char[] c = new char[5];
            for (int i = 0; i < c.length; i++) {
                c[i] = (char) ('A' + new Random().nextInt(26));
            }
            String id = new String(c);
            if (servers.containsKey(id)) continue;
            return id;
        }
    }

    public static class Server {
        public final String id;
        public final Map<Integer, Connection> clients = new ConcurrentHashMap<>();
        public final AtomicInteger idCounter = new AtomicInteger();
        public final Connection owner;

        public Server(String id, Connection owner) {
            this.id = id;
            this.owner = owner;
        }
    }
}
