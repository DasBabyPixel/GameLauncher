package gamelauncher.lwjgl.network;

import gamelauncher.engine.data.DataBuffer;
import gamelauncher.engine.network.packet.PacketEncoder;
import gamelauncher.netty.ByteBufMemory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

class LWJGLNetworkHandler {

    // TODO: Check if this is a valid way for ByteBufs. Releasing etc.
    final ThreadLocal<ByteBufMemory> memory = ThreadLocal.withInitial(() -> new ByteBufMemory(Unpooled.buffer()));
    final PacketEncoder encoder;

    public LWJGLNetworkHandler(PacketEncoder encoder) {
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
