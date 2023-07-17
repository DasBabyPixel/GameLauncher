/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import gamelauncher.engine.network.Connection;
import gamelauncher.engine.network.NetworkAddress;
import gamelauncher.engine.network.packet.PacketEncoder;
import gamelauncher.engine.network.server.ServerListener;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.netty.standalone.WebSocketDecoder;
import gamelauncher.netty.standalone.WebSocketEncoder;
import io.netty.channel.*;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.SSLEngine;
import java.net.http.HttpTimeoutException;
import java.security.cert.X509Certificate;

public class ServerInitializer extends ChannelInitializer<Channel> {
    private final NettyServer server;
    private final NettyNetworkClient client;
    private final Logger logger;

    public ServerInitializer(NettyServer server, NettyNetworkClient client, Logger logger) {
        this.server = server;
        this.client = client;
        this.logger = logger;
    }

    @Override protected void initChannel(@NotNull Channel ch) throws Exception {
        NettyServer.ClientConnection connection = new NettyServer.ClientConnection(client.cached, ch, client);
        connection.remoteAddress(NetworkAddress.bySocketAddress(ch.remoteAddress()));
        connection.localAddress(NetworkAddress.bySocketAddress(ch.localAddress()));
        ch.closeFuture().addListener(f -> {
            ServerListener l = server.serverListener();
            if (l != null) l.disconnected(connection);
            logger.infof("Client disconnected: %s", ch.remoteAddress().toString());
        });
        ChannelPipeline p = ch.pipeline();
        ServerListener l = server.serverListener();
        if (l != null) l.connected(connection);
        logger.infof("Client %s connected on %s", connection.remoteAddress().toString(), connection.localAddress().toString());

        X509Certificate[] certs = new X509Certificate[]{server.keyManagment().certificate};
        String[] protocols = new String[]{"TLSv1.3"};
        SslContext sslContext = SslContextBuilder.forServer(server.keyManagment().privateKey, certs).protocols(protocols).build();
        SSLEngine engine = sslContext.newEngine(p.channel().alloc());
        p.addLast("ssl", new SslHandler(engine));

        ServerWebSocketListener listener = new ServerWebSocketListener(ch.newPromise());
        listener.promise.addListener(f -> {
            if (f.isSuccess()) {
                connection.state.value(Connection.State.CONNECTED);
            } else {
                connection.state.value(Connection.State.CLOSED);
            }
        });

        p.addLast("http", new HttpServerCodec());
        p.addLast("aggregator", new HttpObjectAggregator(65536));
        p.addLast("websocket_protocol", new WebSocketServerProtocolHandler("/orbits/", null, true));
        NettyNetworkHandler handler = new NettyNetworkHandler(new PacketEncoder(client.packetRegistry()));
        p.addLast("websocket_listener", listener);
        p.addLast("websocket_decoder", new WebSocketDecoder());
        p.addLast("packet_decoder", new NettyNetworkDecoder(handler));
        p.addLast("websocket_encoder", new WebSocketEncoder());
        p.addLast("packet_encoder", new NettyNetworkEncoder(handler));
        p.addLast("packet_acceptor", new NettyNetworkAcceptor(client, connection, logger));
        p.addLast("exception_handler", new ExceptionHandler(logger));
    }

    private static class ServerWebSocketListener extends ChannelInboundHandlerAdapter {
        private final ChannelPromise promise;

        public ServerWebSocketListener(ChannelPromise promise) {
            this.promise = promise;
        }

        @Override public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
                promise.setSuccess();
            } else if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_TIMEOUT) {
                promise.setFailure(new HttpTimeoutException("Failed to upgrade to websocket: handshake timed out"));
            }
            super.userEventTriggered(ctx, evt);
        }
    }
}
