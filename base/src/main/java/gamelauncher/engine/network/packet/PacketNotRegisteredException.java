package gamelauncher.engine.network.packet;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public class PacketNotRegisteredException extends GameException {
    public PacketNotRegisteredException(String message) {
        super(message);
    }
}
