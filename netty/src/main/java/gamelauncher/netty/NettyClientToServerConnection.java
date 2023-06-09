/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import gamelauncher.engine.network.Connection;
import gamelauncher.engine.network.NetworkAddress;
import gamelauncher.engine.util.GameException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import java8.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

public class NettyClientToServerConnection extends AbstractConnection {
    private final EventLoopGroup eventLoopGroup;

    public NettyClientToServerConnection(Executor cached, NettyNetworkClient client, NetworkAddress address) {
        super(cached);
        eventLoopGroup = new NioEventLoopGroup();
        init(create(client, address, eventLoopGroup));
    }

    @Override protected CompletableFuture<Void> cleanup0() throws GameException {
        CompletableFuture<Void> f = new CompletableFuture<>();
        super.cleanup0().thenRun(() -> eventLoopGroup.shutdownGracefully().addListener(fut -> {
            if (fut.isSuccess()) f.complete(null);
            else f.completeExceptionally(fut.cause());
        })).exceptionally(t -> {
            eventLoopGroup.shutdownGracefully().addListener(fut -> {
                if (fut.isSuccess()) f.completeExceptionally(t);
                else {
                    Throwable t2 = fut.cause();
                    t2.addSuppressed(t);
                    f.completeExceptionally(t2);
                }
            });
            return null;
        });
        return f;
    }

    private Channel create(NettyNetworkClient client, NetworkAddress address, EventLoopGroup eventLoopGroup) {
        Bootstrap b = new Bootstrap();
        b.remoteAddress(((NetworkAddress.SocketAddressCapableNetworkAddress) address).toSocketAddress()).option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true).group(eventLoopGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<>() {
            @Override protected void initChannel(@NotNull Channel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                client.setupPipeline(p, NettyClientToServerConnection.this, SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build(), NettyNetworkClient.logger);
            }
        });
        remoteAddress(NetworkAddress.bySocketAddress(b.config().remoteAddress()));
        ChannelFuture f = b.connect();
        f.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                localAddress(NetworkAddress.bySocketAddress(future.channel().localAddress()));
                state.value(Connection.State.CONNECTED);
                NettyNetworkClient.logger.infof("Connected to %s", address);
            } else cleanup();
        });
        return f.channel();
    }
}
