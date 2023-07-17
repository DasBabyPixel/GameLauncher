/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslProvider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Map;

public class NettyTest {
    private static BouncyCastleJsseProvider provider = new BouncyCastleJsseProvider();

    public static void main(String[] args) throws NoSuchAlgorithmException, SSLException, KeyManagementException {
        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(provider);
        System.out.println(SecureRandom.getInstance("DEFAULT"));

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.remoteAddress(new InetSocketAddress("ssh.darkcube.eu", 443));
        b.option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true).group(eventLoopGroup).channel(NioSocketChannel.class);
        b.handler(new ChannelInitializer<Channel>() {
            @Override protected void initChannel(Channel ch) throws Exception {
                addWebSocket(ch.pipeline());
                addEncryption(ch.pipeline());
                for (Map.Entry<String, ChannelHandler> entry : ch.pipeline()) {
                    System.out.println(entry.getValue());
                }
            }
        });
        ChannelFuture f = b.connect().syncUninterruptibly();
    }

    private static void addEncryption(ChannelPipeline pipeline) throws SSLException {
        String[] protocols = new String[]{"TLSv1.3"};
        SslContext ctx = SslContextBuilder.forClient().protocols(protocols).sslContextProvider(provider).sslProvider(SslProvider.JDK).build();

        SSLEngine engine = ctx.newEngine(pipeline.channel().alloc(), "ssh.darkcube.eu", 443);
        pipeline.addFirst("ssl", new SslHandler(engine));
    }

    private static void addWebSocket(ChannelPipeline pipeline) throws URISyntaxException {

        pipeline.addLast("http", new HttpClientCodec());


        pipeline.addAfter("http", "aggregator", new HttpObjectAggregator(65536));
        URI uri = new URI("ws", null, "ssh.darkcube.eu", 443, "/orbitsgame.zip", null, null);
        WebSocketClientProtocolConfig clientConfig = WebSocketClientProtocolConfig.newBuilder().webSocketUri(uri).version(WebSocketVersion.V13).allowExtensions(true).subprotocol(null).customHeaders(new DefaultHttpHeaders()).build();
        WebSocketClientProtocolHandler clientHandler = new WebSocketClientProtocolHandler(clientConfig);
        pipeline.addAfter("aggregator", "websocket_protocol", clientHandler);

//        initFuture = clientHandler.handshakeFuture();
//        System.out.println("websocket");
//        clientHandler.handshakeFuture().addListener(f -> {
//            System.out.println("handshake done");
//        });
    }
}
