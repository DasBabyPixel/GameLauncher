/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.network;

import de.dasbabypixel.annotations.Api;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

/**
 * @author DasBabyPixel
 */
public interface NetworkAddress {

    static NetworkAddress byName(String host, int port) throws UnknownHostException {
        return bySocketAddress(new InetSocketAddress(host, port));
    }

    static NetworkAddress bySocketAddress(SocketAddress socketAddress) {
        return new SocketNetworkAddress(socketAddress);
    }

    static NetworkAddress localhost(int port) {
        return bySocketAddress(new InetSocketAddress(SocketNetworkAddress.localhost, port));
    }

    interface SocketAddressCapableNetworkAddress extends NetworkAddress {
        @Api SocketAddress toSocketAddress();
    }
}
