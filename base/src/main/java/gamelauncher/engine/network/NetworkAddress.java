/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.network;

import de.dasbabypixel.annotations.Api;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author DasBabyPixel
 */
public interface NetworkAddress {

    static NetworkAddress byName(String host) throws UnknownHostException {
        return new InetNetworkAddress(InetAddress.getByName(host));
    }

    static NetworkAddress localhost() {
        return InetNetworkAddress.localhost;
    }

    @Api
    class InetNetworkAddress implements NetworkAddress, InetAddressCapableNetworkAddress {
        private static final InetNetworkAddress localhost;

        static {
            try {
                localhost = new InetNetworkAddress(InetAddress.getLocalHost());
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }

        private final InetAddress inetAddress;

        public InetNetworkAddress(InetAddress inetAddress) {
            this.inetAddress = inetAddress;
        }

        @Api public InetAddress inetAddress() {
            return inetAddress;
        }

        @Override public InetAddress toInetAddress() {
            return inetAddress;
        }

        @Override public String toString() {
            return inetAddress.toString();
        }
    }

    interface InetAddressCapableNetworkAddress extends NetworkAddress {
        @Api InetAddress toInetAddress();
    }
}
