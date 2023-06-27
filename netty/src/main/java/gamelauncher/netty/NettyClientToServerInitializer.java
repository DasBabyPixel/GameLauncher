/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import gamelauncher.engine.network.Connection;
import gamelauncher.engine.network.ProxyConfiguration;
import gamelauncher.engine.network.packet.PacketEncoder;
import gamelauncher.engine.network.packet.PacketRegistry;
import gamelauncher.netty.standalone.WebSocketDecoder;
import gamelauncher.netty.standalone.WebSocketEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

public class NettyClientToServerInitializer extends ChannelInitializer<Channel> {

    private final NettyClientToServerConnection.ConnectionType<?> connectionType;
    private final NettyNetworkClient client;
    private final NettyClientToServerConnection connection;
    private final @Nullable String hostname;
    private final int port;
    private final ProxyConfiguration proxy;
    private final PacketRegistry packetRegistry;
    private final boolean useSsl;
    private volatile ChannelFuture initFuture;

    public NettyClientToServerInitializer(NettyClientToServerConnection.ConnectionType<?> connectionType, NettyNetworkClient client, NettyClientToServerConnection connection, @Nullable String hostname, int port, ProxyConfiguration proxy, PacketRegistry packetRegistry, boolean useSsl) {
        this.connectionType = connectionType;
        this.client = client;
        this.connection = connection;
        this.hostname = hostname;
        this.port = port;
        this.proxy = proxy;
        this.packetRegistry = packetRegistry;
        this.useSsl = useSsl;
    }

    @Override protected void initChannel(@NotNull Channel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        NettyNetworkHandler handler = new NettyNetworkHandler(new PacketEncoder(packetRegistry));
        p.addLast("packet_decoder", new NettyNetworkDecoder(handler));
        p.addLast("packet_encoder", new NettyNetworkEncoder(handler));
        p.addLast("packet_acceptor", new NettyNetworkAcceptor(client, connection, NettyNetworkClient.logger));
        p.addLast("exception_handler", new ExceptionHandler(NettyNetworkClient.logger));

        if (useSsl) addEncryption(p);
        if (proxy != null) addProxy(p);

        addConnectionProtocol(p);
    }

    public void connected(Channel channel) {
        if (initFuture == null) {
            connected0(channel);
        } else {
            initFuture.addListener(f -> {
                if (f.isSuccess()) connected0(channel);
                else connection.cleanup();
            });
        }
    }

    private void connected0(Channel channel) {
        connection.state.value(Connection.State.CONNECTED);
        NettyNetworkClient.logger.infof("Connected to %s", channel.remoteAddress());
    }

    private void addConnectionProtocol(ChannelPipeline pipeline) throws URISyntaxException {
        if (connectionType instanceof NettyClientToServerConnection.WebSocket) {
            addWebSocket(pipeline, (NettyClientToServerConnection.WebSocket) connectionType);
        } else if (!(connectionType instanceof NettyClientToServerConnection.Direct)) throw new IllegalArgumentException("Connection Type: " + connectionType);
    }

    private void addWebSocket(ChannelPipeline pipeline, NettyClientToServerConnection.WebSocket webSocket) throws URISyntaxException {
        pipeline.addLast(new LoggingHandler(LogLevel.INFO));

        pipeline.addBefore("packet_decoder", "http", new HttpClientCodec());

        pipeline.addAfter("http", "aggregator", new HttpObjectAggregator(65536));
        URI uri = new URI("ws", null, hostname, port, webSocket.data().path(), null, null);
        WebSocketClientHandler clientHandler = new WebSocketClientHandler(WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));
        pipeline.addAfter("aggregator", "websocket_protocol", clientHandler);
        pipeline.addBefore("packet_decoder", "websocket_decoder", new WebSocketDecoder());
        pipeline.addBefore("packet_encoder", "websocket_encoder", new WebSocketEncoder());

        initFuture = clientHandler.handshakeFuture();
        System.out.println("websocket");
        clientHandler.handshakeFuture().addListener(f -> {
            System.out.println("handshake done");
        });
    }

    private void addEncryption(ChannelPipeline pipeline) throws SSLException {
        SslContext ctx = SslContextBuilder.forClient().protocols("TLSv1.3").sslProvider(SslProvider.JDK).trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        SSLEngine engine = ctx.newEngine(pipeline.channel().alloc(), hostname, port);
        pipeline.addFirst("ssl", new SslHandler(engine));
    }

    private void addProxy(ChannelPipeline pipeline) {
        String host = proxy.hostname();
        int port = proxy.port();
        String user = proxy.username();
        String pass = proxy.password();
        if (user == null) {
            pipeline.addFirst("proxy", new HttpProxyHandler(new InetSocketAddress(host, port)));
        } else if (pass != null) {
            pipeline.addFirst("proxy", new HttpProxyHandler(new InetSocketAddress(host, port), user, pass));
        }
    }
}
