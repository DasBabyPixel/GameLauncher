/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import gamelauncher.engine.network.NetworkAddress;
import gamelauncher.engine.network.packet.PacketEncoder;
import gamelauncher.engine.network.server.ServerListener;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.netty.standalone.WebSocketDecoder;
import gamelauncher.netty.standalone.WebSocketEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.jetbrains.annotations.NotNull;

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
//                    SslContext sslContext = SslContextBuilder.forServer(keyManagment.privateKey, keyManagment.certificate).protocols("TLSv1.3").build();
//                    SSLEngine engine = sslContext.newEngine(p.channel().alloc());
//                    p.addLast("ssl", new SslHandler(engine));

        p.addLast("http", new HttpServerCodec());
        p.addLast("aggregator", new HttpObjectAggregator(65536));
        p.addLast("websocket_protocol", new WebSocketServerProtocolHandler("/orbits/", null, true));
        NettyNetworkHandler handler = new NettyNetworkHandler(new PacketEncoder(client.packetRegistry()));
        p.addLast("websocket_decoder", new WebSocketDecoder());
        p.addLast("packet_decoder", new NettyNetworkDecoder(handler));
        p.addLast("websocket_encoder", new WebSocketEncoder());
        p.addLast("packet_encoder", new NettyNetworkEncoder(handler));
        p.addLast("packet_acceptor", new NettyNetworkAcceptor(client, connection, logger));
        p.addLast("exception_handler", new ExceptionHandler(logger));
    }
}
