/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

import gamelauncher.engine.util.GameException;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.security.*;
import java.util.Arrays;

public class Server {
//    private final NettyNetworkClient client;

    public Server() throws NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException, UnrecoverableKeyException, KeyStoreException {
        BouncyCastleJsseProvider provider = new BouncyCastleJsseProvider();
        SSLContext context = SSLContext.getInstance("TLSv1", provider);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(null, null);
        context.init(kmf.getKeyManagers(), null, new SecureRandom());
        System.out.println(Arrays.toString(context.createSSLEngine().getSupportedProtocols()));
//        this.client = new NettyNetworkClient();
//        client.packetRegistry().register(TestPacket.class, TestPacket::new);
//        client.start();
//        NetworkServer server = client.newServer();
//        server.start();
    }

    public static void main(String[] args) throws GameException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException, UnrecoverableKeyException, KeyStoreException {
//        Basics.setup();
        new Server();
//        Basics.cleanup();
    }
}
