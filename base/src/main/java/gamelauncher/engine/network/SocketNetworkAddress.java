/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.network;

import de.dasbabypixel.annotations.Api;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Objects;

@Api
class SocketNetworkAddress implements NetworkAddress, NetworkAddress.SocketAddressCapableNetworkAddress {
    public static final InetAddress localhost;

    static {
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private final SocketAddress socketAddress;

    public SocketNetworkAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    @Override @Api public SocketAddress toSocketAddress() {
        return socketAddress;
    }

    @Override public String toString() {
        return socketAddress.toString();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocketNetworkAddress that = (SocketNetworkAddress) o;
        return Objects.equals(socketAddress, that.socketAddress);
    }

    @Override public int hashCode() {
        return Objects.hash(socketAddress);
    }
}
