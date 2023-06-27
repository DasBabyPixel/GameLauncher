/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

import com.google.common.base.Charsets;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.proxy.HttpProxyHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.*;

public class ProxyClient {
    public static void main(String[] args) throws IOException, URISyntaxException, NoSuchFieldException, IllegalAccessException {
        System.setProperty("https.protocols", "TLSv1.3");
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
        System.setProperty("java.net.useSystemProxies", "true");
        Field field = ProxySelector.getDefault().getClass().getDeclaredField("hasSystemProxies");
        field.setAccessible(true);
        System.out.println(ProxySelector.getDefault());
        System.out.println(field.get(ProxySelector.getDefault()));
        System.getProperties().forEach((key, val) -> {
            System.out.println(key + ": " + val);
        });
        NetworkInterface.networkInterfaces().forEach(ni -> {
            ni.inetAddresses().forEach(ia -> {
                System.out.println(ni + ": " + ia);
            });
        });

        final String authUser = "lorenz";
        final String authPassword = "IbdL8ibwt!";
        Authenticator.setDefault(new Authenticator() {
            @Override public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(authUser, authPassword.toCharArray());
            }
        });
        Proxy proxy = print(ProxySelector.getDefault());
        InetSocketAddress address = (InetSocketAddress) proxy.address();
        if (proxy.address() != null) System.out.println(proxy.address().getClass());
        Bootstrap b = new Bootstrap();
        b.channelFactory(NioSocketChannel::new);
        b.option(ChannelOption.TCP_NODELAY, true);
        b.handler(new ChannelInitializer<>() {
            @Override protected void initChannel(@NotNull Channel ch) throws Exception {
                ch.pipeline().addLast(new HttpProxyHandler(new InetSocketAddress("192.168.3.13", 8080), authUser, authPassword));
                ch.pipeline().addLast(new HttpClientCodec(), new HttpObjectAggregator(Integer.MAX_VALUE), new ChannelInboundHandlerAdapter() {
                    @Override public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
                        System.out.println("INCOMING: " + msg);
                        if (msg instanceof FullHttpResponse) {
                            FullHttpResponse r = (FullHttpResponse) msg;
                            String s = r.content().toString(Charsets.UTF_8);
//                            System.out.println(s);
                        }
                        super.channelRead(ctx, msg);
                    }
                });
            }
        });
        b.group(new NioEventLoopGroup());
        ChannelFuture cf = b.connect("37.114.47.76", 80);
        cf.addListener(f -> {
            System.out.println(f.isSuccess());
            if (!f.isSuccess()) f.cause().printStackTrace();
            else {
                System.out.println(cf.channel().remoteAddress());

                URL url = new URL("http://37.114.47.76/orbitsgame.zip");
                FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, url.toString());
                request.headers().set(HttpHeaderNames.HOST, "");
                request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP_DEFLATE);
                System.out.println(request);
                // Send the HTTP request.
                cf.channel().writeAndFlush(request).addListener(f2 -> {
                    System.out.println(f2.isSuccess());
                });
            }
        });
    }

    private static Proxy print(ProxySelector selector) throws URISyntaxException, IOException {
        for (Proxy proxy : selector.select(new URI("https://google.com"))) {
            System.out.println(proxy + ": " + proxy.address());

            URL url = new URL("https://google.com");
            URLConnection con = url.openConnection(proxy);

            try {

                String data = new String(con.getInputStream().readAllBytes()).replace('\n', ' ');
                System.out.println(data);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            return proxy;
        }
        throw new IOException();
    }
}
