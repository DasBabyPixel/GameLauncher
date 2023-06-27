/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.network.*;
import gamelauncher.engine.network.packet.Packet;
import gamelauncher.engine.network.packet.PacketHandler;
import gamelauncher.engine.network.packet.PacketRegistry;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.settings.MainSettingSection;
import gamelauncher.engine.util.collections.Collections;
import gamelauncher.engine.util.concurrent.ExecutorThreadService;
import gamelauncher.engine.util.concurrent.WrapperExecutorThreadService;
import gamelauncher.engine.util.logging.Logger;
import java8.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.net.URI;
import java.nio.file.Path;
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

    static final Logger logger = Logger.logger();
    private static final int PORT = 15684;
    final ExecutorService cached;
    final ExecutorThreadService cachedService;
    private final Lock lock = new ReentrantLock(true);
    private final Lock handlerLock = new ReentrantLock(true);
    private final Map<Class<?>, Collection<HandlerEntry<?>>> handlers = new ConcurrentHashMap<>();
    private final PacketRegistry packetRegistry = new PacketRegistry();
    private final List<Connection> connections = new ArrayList<>();
    private final @UnmodifiableView List<Connection> connectionsUnmodifiable = Collections.unmodifiableList(connections);
    private final GameLauncher launcher;
    private final Path sslDirectory;
    private final boolean customCached;
    private ProxyConfiguration proxy;
    private int port = PORT;
    private volatile boolean running = false;

    public NettyNetworkClient(GameLauncher launcher) {
        this.launcher = launcher;
        this.sslDirectory = launcher.dataDirectory().resolve("ssl");
        this.cached = (ExecutorService) (cachedService = launcher.threads().cached).executor();
        this.customCached = false;
    }

    @ApiStatus.Experimental public NettyNetworkClient() {
        this.launcher = null;
        this.sslDirectory = Paths.get("ssl");
        cached = Executors.newCachedThreadPool();
        cachedService = new WrapperExecutorThreadService(cached);
        customCached = true;
    }

    @Override public void start() {
        running = true;
        if (launcher != null) {
            if (launcher.settings().getSetting(MainSettingSection.PROXY_HOST).getValue() != null) {
                String host = launcher.settings().<String>getSetting(MainSettingSection.PROXY_HOST).getValue();
                int port = launcher.settings().<Integer>getSetting(MainSettingSection.PROXY_PORT).getValue();
                String user = launcher.settings().<String>getSetting(MainSettingSection.PROXY_USERNAME).getValue();
                String pass = launcher.settings().<String>getSetting(MainSettingSection.PROXY_PASSWORD).getValue();
                this.proxy = new ProxyConfiguration() {
                    @Override public @NotNull String hostname() {
                        return host;
                    }

                    @Override public int port() {
                        return port;
                    }

                    @Override public @Nullable String username() {
                        return user;
                    }

                    @Override public @Nullable String password() {
                        return pass;
                    }
                };
            }
        }
    }

    @Override public void stop() {
        running = false;
        // Also nothing to do
    }

    @Override public boolean running() {
        return running;
    }

    @Override public NettyServer newServer() {
        return new NettyServer(this, sslDirectory);
    }

    @Override public ProxyConfiguration proxy() {
        return proxy;
    }

    @Override public void proxy(ProxyConfiguration proxy) {
        this.proxy = proxy;
    }

    @Override public LanDetector createLanDetector(LanDetector.ClientHandler clientHandler) {
        throw new UnsupportedOperationException();
    }

    @Override public Connection connect(NetworkAddress address) {
        try {
            lock.lock();
            NettyClientToServerConnection connection = new NettyClientToServerConnection(cached, this, address);
            connections.add(connection);
            return connection;
        } finally {
            lock.unlock();
        }
    }

    @Override public Connection connect(URI uri) {
        try {
            lock.lock();
            NettyClientToServerConnection connection = new NettyClientToServerConnection(cached, this, uri);
            connections.add(connection);
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
        return port;
    }

    public void port(int port) {
        this.port = port;
    }

    @Override public PacketRegistry packetRegistry() {
        return packetRegistry;
    }

    public void handleIncomingPacket(AbstractConnection connection, Packet packet, Logger logger) {
        boolean handled = connection.handle(packet);
        Collection<HandlerEntry<?>> col = handlers.get(packet.getClass());
        if (col == null) {
            if (!handled) logger.info("Received unhandled packet: " + packet.getClass() + ", " + packet);
            return;
        }
        for (HandlerEntry<?> h : col) {
            h.receivePacket(connection, packet);
        }
    }

    @Override protected CompletableFuture<Void> cleanup0() {
        if (!customCached) return launcher.threads().cached.submit(() -> {
            stop();
            try {
                lock.lock();
                for (Connection connection : new ArrayList<>(connections)) connection.cleanup();
            } finally {
                lock.unlock();
            }
        });

        cached.shutdown();
        return null; // Let's be honest if we are using a custom client atm we terminate the process when the client is closed so what's the point in implementing this?
    }

    void remove(NettyClientToServerConnection connection) {
        try {
            lock.lock();
            connections.remove(connection);
        } finally {
            lock.unlock();
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
