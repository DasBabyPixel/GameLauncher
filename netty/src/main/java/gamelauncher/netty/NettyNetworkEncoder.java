/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import gamelauncher.engine.data.DataBuffer;
import gamelauncher.engine.data.DataUtil;
import gamelauncher.engine.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author DasBabyPixel
 */
public class NettyNetworkEncoder extends MessageToByteEncoder<Packet> {

    private final NettyNetworkHandler handler;

    public NettyNetworkEncoder(NettyNetworkHandler handler) {
        this.handler = handler;
    }

    @Override public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }

    @Override protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
        DataBuffer buf = handler.prepareBuffer(out);
        int index = buf.increaseWriterIndex(DataUtil.BYTES_INT);
        int packetIndex = buf.writerIndex();
        handler.encoder.write(buf, msg);
        int packetSize = buf.writerIndex() - packetIndex;
        buf.memory().setInt(index, packetSize);
        handler.finishBuffer(buf);
    }

}
