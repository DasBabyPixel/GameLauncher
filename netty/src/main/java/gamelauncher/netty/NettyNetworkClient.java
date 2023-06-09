/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.network.Connection;
import gamelauncher.engine.network.LanDetector;
import gamelauncher.engine.network.NetworkAddress;
import gamelauncher.engine.network.NetworkClient;
import gamelauncher.engine.network.packet.Packet;
import gamelauncher.engine.network.packet.PacketEncoder;
import gamelauncher.engine.network.packet.PacketHandler;
import gamelauncher.engine.network.packet.PacketRegistry;
import gamelauncher.engine.network.packet.packets.PacketIdPacket;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.collections.Collections;
import gamelauncher.engine.util.logging.Logger;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import java8.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnmodifiableView;

import javax.net.ssl.SSLEngine;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NettyNetworkClient extends AbstractGameResource implements NetworkClient {

    private static final int PORT = 15684;

    static final Logger logger = Logger.logger();
    private final Lock lock = new ReentrantLock(true);

    private final Lock handlerLock = new ReentrantLock(true);
    private final Map<Class<?>, Collection<HandlerEntry<?>>> handlers = new ConcurrentHashMap<>();
    private final PacketRegistry packetRegistry = new PacketRegistry();
    private final NettyNetworkHandler handler = new NettyNetworkHandler(new PacketEncoder(packetRegistry));
    private final List<Connection> connections = new ArrayList<>();
    private final @UnmodifiableView List<Connection> connectionsUnmodifiable = Collections.unmodifiableList(connections);
    private final GameLauncher launcher;
    private final NettyServer server;
    final ExecutorService cached;
    private final boolean customCached;
    private volatile boolean running = false;

    public NettyNetworkClient(GameLauncher launcher) {
        this.launcher = launcher;
        this.server = new NettyServer(this, launcher.dataDirectory().resolve("ssl"));
        this.cached = (ExecutorService) launcher.threads().cached.executor();
        this.customCached = false;
    }

    @Deprecated @ApiStatus.Experimental public NettyNetworkClient() {
        this.launcher = null;
        this.server = new NettyServer(this, Paths.get("ssl"));
        cached = Executors.newCachedThreadPool();
        customCached = true;
        initPackets();
    }

    private void initPackets() {
        packetRegistry.register(PacketIdPacket.class, PacketIdPacket::new);
    }

    public void setupPipeline(ChannelPipeline p, Connection connection, SslContext sslContext, Logger logger) {
        SSLEngine engine = sslContext.newEngine(p.channel().alloc());
        p.addLast("ssl", new SslHandler(engine));
        p.addLast("packet_decoder", new NettyNetworkDecoder(handler));
        p.addLast("packet_encoder", new NettyNetworkEncoder(handler));
        p.addLast("packet_acceptor", new NettyNetworkAcceptor(this, connection, logger));
        p.addLast("exception_handler", new ExceptionHandler(logger));
    }

    @Override public void start() {
        running = true;
        // Nothing to do lol
    }

    @Override public void stop() {
        running = false;
        // Also nothing to do
    }

    @Override protected CompletableFuture<Void> cleanup0() {
        if (!customCached) return launcher.threads().cached.submit(() -> {
            stop();
            try {
                lock.lock();
                for (Connection connection : connections) connection.cleanup();
            } finally {
                lock.unlock();
            }
        });

        cached.shutdown();
        return null; // Let's be honest if we are using a custom client atm we terminate the process when the client is closed so what's the point in implementing this?
    }

    @Override public boolean running() {
        return running;
    }

    @Override public NettyServer server() {
        return server;
    }

    @Override public LanDetector createLanDetector(LanDetector.ClientHandler clientHandler) {
        throw new UnsupportedOperationException();
    }

    @Override public Connection connect(NetworkAddress address) {
        try {
            lock.lock();
            NettyClientToServerConnection connection = new NettyClientToServerConnection(cached, this, address);
            connections.add(connection);
            connection.cleanupFuture().thenRun(() -> {
                try {
                    lock.lock();
                    connections.remove(connection);
                } finally {
                    lock.unlock();
                }
            });
            return connection;
        } finally {
            lock.unlock();
        }
    }

    @Override public @UnmodifiableView List<Connection> connections() {
        try {
            lock.lock();
            return connectionsUnmodifiable;
        } finally {
            lock.unlock();
        }
    }

    @Override public <T extends Packet> void addHandler(Class<T> packetTpye, PacketHandler<T> handler) {
        handlerLock.lock();
        if (!handlers.containsKey(packetTpye)) {
            handlers.put(packetTpye, ConcurrentHashMap.newKeySet());
        }
        handlers.get(packetTpye).add(new HandlerEntry<>(packetTpye, handler));
        handlerLock.unlock();
    }

    @Override public <T extends Packet> void removeHandler(Class<T> packetType, PacketHandler<T> handler) {
        handlerLock.lock();
        if (handlers.containsKey(packetType)) {
            Collection<HandlerEntry<?>> col = handlers.get(packetType);
            col.removeIf(he -> he.clazz() == packetType && he.handler == handler);
            if (col.isEmpty()) handlers.remove(packetType);

        }
        handlerLock.unlock();
    }

    public int port() {
        return PORT;
    }

    @Override public PacketRegistry packetRegistry() {
        return packetRegistry;
    }

    public void handleIncomingPacket(Connection connection, Packet packet, Logger logger) {
        Collection<HandlerEntry<?>> col = handlers.get(packet.getClass());
        if (col == null) {
            logger.info("Received unhandled packet: " + packet.getClass() + ", " + packet);
            return;
        }
        for (HandlerEntry<?> h : col) {
            h.receivePacket(connection, packet);
        }
    }

    static class HandlerEntry<T extends Packet> {
        private final Class<T> clazz;
        private final PacketHandler<T> handler;

        public HandlerEntry(Class<T> clazz, PacketHandler<T> handler) {
            this.clazz = clazz;
            this.handler = handler;
        }

        public Class<T> clazz() {
            return clazz;
        }

        public void receivePacket(Connection connection, Object packet) {
            handler.receivePacket(connection, clazz.cast(packet));
        }

    }
}
