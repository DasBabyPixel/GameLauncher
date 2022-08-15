package gamelauncher.lwjgl.network;

import gamelauncher.engine.network.packet.Packet;
import gamelauncher.engine.network.packet.PacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author DasBabyPixel
 */
public class LWJGLNetworkEncoder extends MessageToByteEncoder<Packet> {

	private final LWJGLNetworkHandler handler;

	/**
	 * @param handler
	 */
	public LWJGLNetworkEncoder(LWJGLNetworkHandler handler) {
		this.handler = handler;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
		PacketBuffer buf = handler.prepareBuffer(out);
		int index = buf.increaseWriterIndex(Integer.BYTES);
		int packetIndex = buf.writerIndex();
		handler.encoder.write(buf, msg);
		int packetSize = buf.writerIndex() - packetIndex;
		buf.getMemory().setInt(index, packetSize);
	}

}
