package gamelauncher.lwjgl.network;

import gamelauncher.engine.network.packet.PacketBuffer;
import gamelauncher.engine.network.packet.PacketEncoder;
import io.netty.buffer.ByteBuf;

class LWJGLNetworkHandler {

	final ThreadLocal<PacketBuffer> buffer = ThreadLocal.withInitial(() -> new PacketBuffer(null));

	final ThreadLocal<ByteBufMemory> memory = ThreadLocal.withInitial(() -> new ByteBufMemory(null));

	final PacketEncoder encoder;

	public LWJGLNetworkHandler(PacketEncoder encoder) {
		this.encoder = encoder;
	}

	PacketBuffer prepareBuffer(ByteBuf buf) {
		ByteBufMemory memory = this.memory.get();
		memory.buf = buf;
		PacketBuffer pbuf = buffer.get();
		pbuf.setMemory(memory);
		pbuf.writerIndex(memory.buf.writerIndex());
		pbuf.readerIndex(memory.buf.readerIndex());
		return pbuf;
	}

}
