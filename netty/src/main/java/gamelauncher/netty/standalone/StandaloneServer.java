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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class StandaloneServer {
    private static final Key KEY_IDS = new Key("ids");
    private static final Key KEY_SERVER = new Key("server");
    private static final Key KEY_SERVER_ID = new Key("server");
    private static final Map<String, Server> servers = new HashMap<>();
    private static final Logger logger = Logger.logger();
    private static final int PORT = Integer.getInteger("gamelauncher.standalone.port", 19452);

    public static void main(String[] args) throws GameException {
        setup();
        NettyNetworkClient client = new NettyNetworkClient();
        client.port(PORT);
        registerPackets(client.packetRegistry());
        client.start();
        client.addHandler(PacketRequestServerId.class, (connection, packet) -> {
            synchronized (servers) {
                String id = newId();
                servers.put(id, new Server(id, connection));
                Collection<String> ids = connection.storedValue(KEY_IDS, ArrayList::new);
                ids.add(id);
                logger.infof("%s: New Server: %s", connection.remoteAddress(), id);
                connection.sendPacket(new PacketRequestServerId.Response(id));
            }
        });
        client.addHandler(PacketConnectToServer.class, (connection, packet) -> {
            String serverId = connection.storedValue(KEY_SERVER);
            if (serverId == null) {
                Server s = servers.get(packet.id);
                if (s != null) {
                    connection.storeValue(KEY_SERVER_ID, s.idCounter.incrementAndGet());
                    connection.storeValue(KEY_SERVER, packet.id);
                    s.clients.put(connection.<Integer>storedValue(KEY_SERVER_ID), connection);
                    s.owner.sendPacket(new PacketClientConnected(connection.<Integer>storedValue(KEY_SERVER_ID)));
                    logger.infof("%s: Connected to Server: %s", connection.remoteAddress(), packet.id);
                    return;
                }
            }
            try {
                connection.cleanup();
            } catch (GameException e) {
                throw new RuntimeException(e);
            }
        });
        client.addHandler(PacketShutdownServer.class, (connection, packet) -> {
            synchronized (servers) {
                Collection<String> ids = connection.storedValue(KEY_IDS);
                if (ids == null) return;
                if (!ids.contains(packet.id)) return;
                ids.remove(packet.id);
                Server s = servers.remove(packet.id);
                shutdown(s);
            }
        });
        client.addHandler(PacketPayloadOutC2S.class, (connection, packet) -> {
            String serverId = connection.storedValue(KEY_SERVER);
            if (serverId == null) return;
            synchronized (servers) {
                Server s = servers.get(serverId);
                if (s == null) return;
                s.owner.sendPacket(new PacketPayloadInC2S(connection.<Integer>storedValue(KEY_SERVER_ID), packet.data));
            }
        });
        client.addHandler(PacketPayloadOutS2C.class, (connection, packet) -> {
            synchronized (servers) {
                Server s = servers.get(packet.serverId);
                if (s == null) return;
                Connection con = s.clients.get(packet.target);
                if (con == null) return;
                con.sendPacket(new PacketPayloadInS2C(packet.data));
            }
        });
        client.addHandler(PacketPayloadOutS2A.class, (connection, packet) -> {
            synchronized (servers) {
                Server s = servers.get(packet.serverId);
                if (s == null) return;
                PacketPayloadInS2C payload = new PacketPayloadInS2C(packet.data);
                for (Connection con : s.clients.values()) con.sendPacket(payload);
            }
        });

        NettyServer server = client.newServer();
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

    private static void shutdown(Server server) {
        for (Connection connection : server.clients.values()) {
            try {
                connection.cleanup();
            } catch (GameException e) {
                throw new RuntimeException(e);
            }
        }
        logger.infof("Server Shutdown: %s", server.id);
        server.clients.clear();
        try {
            if (!server.owner.cleanedUp()) server.owner.cleanup();
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
    }

    private static String newId() {
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

    public static void registerPackets(PacketRegistry registry) {
        registry.register(PacketRequestServerId.class, PacketRequestServerId::new);
        registry.register(PacketRequestServerId.Response.class, PacketRequestServerId.Response::new);
        registry.register(PacketConnectToServer.class, PacketConnectToServer::new);
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
        Logger.Initializer.init(null);
        Logger.asyncLogStream().start();
    }

    public static void cleanup() throws GameException {
        Threads.await(Logger.asyncLogStream().cleanup());
    }

    private static class Server {
        private final String id;
        private final Map<Integer, Connection> clients = new ConcurrentHashMap<>();
        private final AtomicInteger idCounter = new AtomicInteger();
        private final Connection owner;

        public Server(String id, Connection owner) {
            this.id = id;
            this.owner = owner;
        }
    }
}
