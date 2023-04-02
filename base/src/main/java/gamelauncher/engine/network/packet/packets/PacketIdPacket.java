package gamelauncher.engine.network.packet.packets;

import gamelauncher.engine.network.packet.Packet;
import gamelauncher.engine.network.packet.PacketBuffer;

public class PacketIdPacket extends Packet {

	public int id;

	public PacketIdPacket() {
		super("packet_id");
	}

	public PacketIdPacket(int id) {
		this();
		this.id = id;
	}

	@Override
	protected void write0(PacketBuffer buffer) {
		buffer.writeInt(id);
	}

	@Override
	protected void read0(PacketBuffer buffer) {
		id = buffer.readInt();
	}

}
