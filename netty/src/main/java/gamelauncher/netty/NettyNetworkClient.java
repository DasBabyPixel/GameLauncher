/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.network.NetworkAddress;
import gamelauncher.engine.network.NetworkClient;
import gamelauncher.engine.network.packet.Packet;
import gamelauncher.engine.network.packet.PacketEncoder;
import gamelauncher.engine.network.packet.PacketHandler;
import gamelauncher.engine.network.packet.PacketRegistry;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.logging.Logger;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import java8.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NettyNetworkClient extends AbstractGameResource implements NetworkClient {

    public static final int PORT = 15684;

    private static final Logger logger = Logger.logger();

    private final Lock lock = new ReentrantLock(true);

    private final Lock handlerLock = new ReentrantLock(true);
    private final Map<Class<?>, Collection<HandlerEntry<?>>> handlers = new ConcurrentHashMap<>();
    private final PacketRegistry packetRegistry = new PacketRegistry();
    private final NettyNetworkHandler handler = new NettyNetworkHandler(new PacketEncoder(packetRegistry));
    private final KeyManagment keyManagment;
    private final boolean connected = false;
    private EventLoopGroup serverBossGroup;
    private EventLoopGroup serverChildGroup;
    private EventLoopGroup clientGroup;
    private volatile boolean running = false;

    public NettyNetworkClient(GameLauncher launcher) {
        this.keyManagment = new KeyManagment(launcher);
    }

    public void setupPipeline(ChannelPipeline p, SslContext sslContext) throws SSLException {
        SSLEngine engine = sslContext.newEngine(p.channel().alloc());
        p.addLast("ssl", new SslHandler(engine));
        p.addLast("packet_decoder", new NettyNetworkDecoder(handler));
        p.addLast("packet_acceptor", new NettyNetworkAcceptor(NettyNetworkClient.this));
        p.addLast("packet_encoder", new NettyNetworkEncoder(handler));
    }

    @Override public void startClient() {
        try {
            lock.lock();
            if (running) {
                return;
            }
            serverBossGroup = new NioEventLoopGroup();
            serverChildGroup = new NioEventLoopGroup();
            ServerBootstrap b = new ServerBootstrap().group(serverBossGroup, serverChildGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<>() {
                @Override public void initChannel(@NotNull Channel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    setupPipeline(p, SslContextBuilder.forServer(keyManagment.privateKey, keyManagment.certificate).build());
                }
            }).option(ChannelOption.SO_KEEPALIVE, true).option(ChannelOption.TCP_NODELAY, true);
            Channel ch = b.bind(PORT).syncUninterruptibly().channel();
            ch.closeFuture().addListener(future -> {

            });
            running = true;
        } finally {
            lock.unlock();
        }
    }

    @Override public void stopClient() {
        try {
            lock.lock();
            running = false;
            serverBossGroup.shutdownGracefully().syncUninterruptibly();
            serverChildGroup.shutdownGracefully().syncUninterruptibly();
        } finally {
            lock.unlock();
        }
    }

    @Override public boolean running() {
        return running;
    }

    @Override public boolean server() {
        return true;
    }

    @Override public boolean connected() {
        return connected;
    }

    @Override public CompletableFuture<Boolean> connect(NetworkAddress address) {
        try {
            lock.lock();
            if (clientGroup != null) {
                disconnect();
            }
            Bootstrap b = new Bootstrap();
            return null;
        } finally {
            lock.unlock();
        }
    }

    @Override public void disconnect() {
        try {
            lock.lock();
            if (clientGroup == null) {
            }

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
            col.removeIf(he -> he.clazz.equals(packetType));
            if (col.isEmpty()) {
                handlers.remove(packetType);
            }
        }
        handlerLock.unlock();
    }

    @Override public PacketRegistry packetRegistry() {
        return packetRegistry;
    }

    public void handleIncomingPacket(Packet packet) {
        Collection<HandlerEntry<?>> col = handlers.get(packet.getClass());
        if (col == null) {
            logger.info("Received unhandled packet: " + packet.getClass());
            return;
        }
        for (HandlerEntry<?> h : col) {
            h.receivePacket(packet);
        }
    }

    @Override protected CompletableFuture<Void> cleanup0() throws GameException {
        return null;
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

        public PacketHandler<T> handler() {
            return handler;
        }

        public void receivePacket(Object packet) {
            handler.receivePacket(clazz.cast(packet));
        }

    }
}
