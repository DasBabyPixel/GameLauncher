package gamelauncher.engine.network.packet;

/**
 * @author DasBabyPixel
 */
public class PacketEncoder {

	private final PacketRegistry registry;

	/**
	 * @param registry
	 */
	public PacketEncoder(PacketRegistry registry) {
		this.registry = registry;
	}

	/**
	 * @param buffer
	 * @param packet
	 * @throws PacketNotRegisteredException
	 */
	public void write(PacketBuffer buffer, Packet packet) throws PacketNotRegisteredException {
		int id = packet.getKey().hashCode();
		registry.getPacketType(id);
		buffer.writeInt(id);
		packet.write(buffer);
	}

	/**
	 * @param buffer
	 * @return the read packet
	 * @throws PacketNotRegisteredException
	 */
	public Packet read(PacketBuffer buffer) throws PacketNotRegisteredException {
		int id = buffer.readInt();
		Packet packet = registry.createPacket(registry.getPacketType(id));
		packet.read(buffer);
		return packet;
	}

}
