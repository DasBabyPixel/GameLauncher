package gamelauncher.netty;

import gamelauncher.engine.data.DataBuffer;
import gamelauncher.engine.network.packet.PacketEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

class NettyNetworkHandler {

    // TODO: Check if this is a valid way for ByteBufs. Releasing etc.
    final ThreadLocal<ByteBufMemory> memory = ThreadLocal.withInitial(() -> new ByteBufMemory(Unpooled.buffer()));
    final PacketEncoder encoder;

    public NettyNetworkHandler(PacketEncoder encoder) {
        this.encoder = encoder;
    }

    DataBuffer prepareBuffer(ByteBuf buf) {
        ByteBufMemory memory = this.memory.get();
        memory.buf = buf;
        DataBuffer pbuf = new DataBuffer(memory);
        pbuf.writerIndex(memory.buf.writerIndex());
        pbuf.readerIndex(memory.buf.readerIndex());
        return pbuf;
    }

}
