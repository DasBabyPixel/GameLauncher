/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import gamelauncher.engine.data.DataBuffer;
import gamelauncher.engine.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author DasBabyPixel
 */
public class NettyNetworkDecoder extends ByteToMessageDecoder {

    private final NettyNetworkHandler handler;

    public NettyNetworkDecoder(NettyNetworkHandler handler) {
        this.handler = handler;
    }

    @Override protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        DataBuffer buf = handler.prepareBuffer(in);
        if (buf.readableBytes() < Integer.BYTES) return;

        int startIndex = buf.readerIndex();
        int size = buf.readInt();
        try {
            if (buf.readableBytes() < size) {
                buf.readerIndex(startIndex);
                return;
            }
            Packet packet = handler.encoder.read(buf);
            handler.finishBuffer(buf);
            out.add(packet);
        } catch (Exception ex) {
            buf.readerIndex(startIndex + Integer.BYTES + size);
            throw ex;
        }
    }
}
