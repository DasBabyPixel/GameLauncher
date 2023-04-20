package gamelauncher.engine.network.packet;

/**
 * @param <T>
 * @author DasBabyPixel
 */
public interface PacketHandler<T extends Packet> {

    void receivePacket(T packet);

}
