package gamelauncher.lwjgl.network;

import gamelauncher.engine.network.packet.Packet;
import gamelauncher.engine.network.packet.PacketBuffer;
import gamelauncher.engine.util.logging.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author DasBabyPixel
 */
public class LWJGLNetworkDecoder extends ByteToMessageDecoder {

	private static final Logger logger = Logger.logger(LWJGLNetworkDecoder.class);

	private final LWJGLNetworkHandler handler;

	public LWJGLNetworkDecoder(LWJGLNetworkHandler handler) {
		this.handler = handler;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
			throws Exception {
		PacketBuffer buf = handler.prepareBuffer(in);
		if (buf.readableBytes() < 4) {
			return;
		}
		int startIndex = buf.readerIndex();
		int size = buf.readInt();
		if (buf.readableBytes() < size) {
			buf.readerIndex(startIndex);
			return;
		}
		Packet packet = handler.encoder.read(buf);
		out.add(packet);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error(cause);
		ctx.close();
	}

}
