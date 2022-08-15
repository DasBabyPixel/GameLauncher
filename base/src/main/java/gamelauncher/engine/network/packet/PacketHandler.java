package gamelauncher.engine.network.packet;

/**
 * @author DasBabyPixel
 * @param <T>
 */
public interface PacketHandler<T extends Packet> {

	/**
	 * @param packet
	 */
	void receivePacket(T packet);

}
