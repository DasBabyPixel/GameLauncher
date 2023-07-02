/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import gamelauncher.engine.data.DataBuffer;
import gamelauncher.engine.network.packet.PacketEncoder;
import io.netty.buffer.ByteBuf;

class NettyNetworkHandler {

    // TODO: Check if this is a valid way for ByteBufs. Releasing etc.
    static final ThreadLocal<ByteBufMemory> memory = ThreadLocal.withInitial(ByteBufMemory::new);
    final PacketEncoder encoder;

    public NettyNetworkHandler(PacketEncoder encoder) {
        this.encoder = encoder;
    }

    DataBuffer prepareBuffer(ByteBuf buf) {
        ByteBufMemory memory = NettyNetworkHandler.memory.get();
        memory.buf = buf;
        DataBuffer pbuf = new DataBuffer(memory);
        pbuf.writerIndex(memory.buf.writerIndex());
        pbuf.readerIndex(memory.buf.readerIndex());
        return pbuf;
    }

    void finishBuffer(DataBuffer pbuf) {
        ((ByteBufMemory) pbuf.memory()).buf.writerIndex(pbuf.writerIndex());
        ((ByteBufMemory) pbuf.memory()).buf.readerIndex(pbuf.readerIndex());
    }

    void clear(DataBuffer buffer) {
        ((ByteBufMemory) buffer.memory()).buf = null;
        buffer.clear();
    }

}
