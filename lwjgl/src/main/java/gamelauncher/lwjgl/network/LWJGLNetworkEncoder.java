package gamelauncher.lwjgl.network;

import gamelauncher.engine.data.DataBuffer;
import gamelauncher.engine.data.DataUtil;
import gamelauncher.engine.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author DasBabyPixel
 */
public class LWJGLNetworkEncoder extends MessageToByteEncoder<Packet> {

    private final LWJGLNetworkHandler handler;

    public LWJGLNetworkEncoder(LWJGLNetworkHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
        DataBuffer buf = handler.prepareBuffer(out);
        int index = buf.increaseWriterIndex(DataUtil.BYTES_INT);
        int packetIndex = buf.writerIndex();
        handler.encoder.write(buf, msg);
        int packetSize = buf.writerIndex() - packetIndex;
        buf.memory().setInt(index, packetSize);
    }

}
