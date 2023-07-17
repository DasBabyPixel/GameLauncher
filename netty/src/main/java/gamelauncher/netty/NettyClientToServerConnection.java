/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import gamelauncher.engine.network.NetworkAddress;
import gamelauncher.engine.network.ProxyConfiguration;
import gamelauncher.engine.util.GameException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java8.util.concurrent.CompletableFuture;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.net.URI;
import java.util.concurrent.Executor;

public class NettyClientToServerConnection extends AbstractConnection {
    private final EventLoopGroup eventLoopGroup;

    public NettyClientToServerConnection(Executor cached, NettyNetworkClient client, NetworkAddress address) {
        super(cached, client);
        eventLoopGroup = new NioEventLoopGroup();
        init(create(client, address, eventLoopGroup));
    }

    public NettyClientToServerConnection(Executor cached, NettyNetworkClient client, URI address) {
        super(cached, client);
        eventLoopGroup = new NioEventLoopGroup();
        init(create(client, address, eventLoopGroup));
    }

    @Override protected CompletableFuture<Void> cleanup0() throws GameException {
        networkClient().remove(this);
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

    private Channel create(NettyNetworkClient client, Object address, EventLoopGroup eventLoopGroup) {
        Bootstrap b = new Bootstrap();
        String host = null;
        int port = -1;
        ProxyConfiguration proxy = client.proxy();
        boolean useSsl = false;
        ConnectionType<?> connectionType;
        if (address instanceof NetworkAddress.SocketAddressCapableNetworkAddress) {
            SocketAddress a = ((NetworkAddress.SocketAddressCapableNetworkAddress) address).toSocketAddress();
            b.remoteAddress(a);
            if (a instanceof InetSocketAddress) {
                InetSocketAddress i = (InetSocketAddress) a;
                host = i.getHostName();
                port = i.getPort();
            }
            connectionType = new WebSocket(new WebSocket.WebSocketData("/"));
        } else if (address instanceof URI) {
            URI uri = (URI) address;
            host = uri.getHost();
            port = uri.getPort();
            if ("https".equalsIgnoreCase(uri.getScheme())) useSsl = true;

            if (port == -1) {
                try {
                    port = uri.toURL().getDefaultPort();
                } catch (MalformedURLException ignored) {
                }
            }
            b.remoteAddress(uri.getHost(), port);
            connectionType = new WebSocket(new WebSocket.WebSocketData(uri.getRawPath()));
        } else {
            throw new IllegalArgumentException(address.toString());
        }

        b.option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true).group(eventLoopGroup).channel(NioSocketChannel.class);
        NettyClientToServerInitializer initializer = new NettyClientToServerInitializer(connectionType, client, this, host, port, proxy, client.packetRegistry(), useSsl);
        b.handler(initializer);

        remoteAddress(NetworkAddress.bySocketAddress(b.config().remoteAddress()));

        NettyNetworkClient.logger.infof("Connecting to %s", b.config().remoteAddress());
        ChannelFuture f = b.connect();
        f.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                localAddress(NetworkAddress.bySocketAddress(future.channel().localAddress()));
                initializer.connected(future.channel());
            } else cleanup();
        });
        return f.channel();
    }

    public interface ConnectionType<T> {
        T data();
    }

    static class Direct implements ConnectionType<Direct.DirectData> {
        private final DirectData data = new DirectData();

        @Override public Direct.DirectData data() {
            return data;
        }

        static class DirectData {
        }
    }

    static class WebSocket implements ConnectionType<WebSocket.WebSocketData> {

        private final WebSocketData data;

        public WebSocket(WebSocketData data) {
            this.data = data;
        }

        @Override public WebSocketData data() {
            return data;
        }

        static class WebSocketData {
            private final String path;

            public WebSocketData(String path) {
                this.path = path;
            }

            public String path() {
                return path;
            }
        }
    }
}
