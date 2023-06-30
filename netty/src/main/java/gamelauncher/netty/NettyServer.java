/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.network.server.NetworkServer;
import gamelauncher.engine.network.server.ServerListener;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.engine.util.property.PropertyUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java8.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NettyServer extends AbstractGameResource implements NetworkServer {
    private static final Logger logger = Logger.logger();
    private final Property<State> state = Property.withValue(State.OFFLINE);
    private final Property<State> stateUnmodifiable = PropertyUtil.unmodifiable(state);
    private final NettyNetworkClient client;
    private final Lock lock = new ReentrantLock();
    private final KeyManagment keyManagment;
    private @Nullable ServerListener serverListener;
    private @Nullable EventLoopGroup bossGroup;
    private @Nullable EventLoopGroup childGroup;
    private @Nullable Channel channel;

    public NettyServer(NettyNetworkClient client, Path sslDirectory) {
        this.client = client;
        this.keyManagment = new KeyManagment(sslDirectory);
    }

    @Override public Property<State> state() {
        return stateUnmodifiable;
    }

    @Override public CompletableFuture<StartResult> start() {
        try {
            lock.lock();
            State state = this.state.value();
            if (state != State.OFFLINE) {
                return CompletableFuture.completedFuture(new StartResult.Failure(new IllegalStateException(state.toString())));
            }
            this.state.value(State.STARTING);
            if (!keyManagment.loaded()) keyManagment.load();
            bossGroup = new NioEventLoopGroup();
            childGroup = new NioEventLoopGroup();
            ServerBootstrap b = new ServerBootstrap().group(bossGroup, childGroup).channel(NioServerSocketChannel.class).childHandler(new ServerInitializer(this, client, logger)).childOption(ChannelOption.TCP_NODELAY, true).childOption(ChannelOption.SO_KEEPALIVE, true);
            CompletableFuture<StartResult> fut = new CompletableFuture<>();
            ChannelFuture chf = b.bind(client.port());
            channel = chf.channel();
            channel.closeFuture().addListener(future -> logger.info("Server closed"));
            chf.addListener(future -> {
                if (future.isSuccess()) {
                    this.state.value(State.RUNNING);
                    fut.complete(new StartResult.Success());
                    logger.info("Server started on " + channel.localAddress());
                } else {
                    logger.error("Server failed to start");
                    stop();
                    this.state.value(State.OFFLINE);
                    fut.complete(new StartResult.Failure(future.cause()));
                }
            });
            return fut;
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("DataFlowIssue") @Override public void stop() {
        try {
            lock.lock();
            if (bossGroup == null) return;
            state.value(State.STOPPING);
            channel.close().awaitUninterruptibly(1, TimeUnit.SECONDS);
            channel = null;
            bossGroup.shutdownGracefully();
            bossGroup = null;
            childGroup.shutdownGracefully();
            childGroup = null;
            state.value(State.OFFLINE);
        } finally {
            lock.unlock();
        }
    }

    @Override public void serverListener(@Nullable ServerListener serverListener) {
        this.serverListener = serverListener;
    }

    public KeyManagment keyManagment() {
        return keyManagment;
    }

    @Override public @Nullable ServerListener serverListener() {
        return serverListener;
    }

    @Override protected CompletableFuture<Void> cleanup0() throws GameException {
        return client.cachedService.submit(this::stop);
    }

    public static class ClientConnection extends AbstractConnection {
        public ClientConnection(Executor cached, Channel channel, NettyNetworkClient client) {
            super(cached, client);
            init(channel);
        }
    }
}
