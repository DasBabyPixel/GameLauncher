package gamelauncher.engine.network;

import de.dasbabypixel.annotations.Api;

import java.net.InetAddress;

/**
 * @author DasBabyPixel
 */
public interface NetworkAddress {

    @Api
    class InetNetworkAddress implements NetworkAddress {
        private final InetAddress inetAddress;

        public InetNetworkAddress(InetAddress inetAddress) {
            this.inetAddress = inetAddress;
        }

        @Api public InetAddress inetAddress() {
            return inetAddress;
        }
    }
}
