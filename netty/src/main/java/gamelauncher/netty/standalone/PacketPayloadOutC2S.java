/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty.standalone;

import gamelauncher.engine.data.DataBuffer;
import gamelauncher.engine.network.packet.Packet;

public class PacketPayloadOutC2S extends Packet {
    public byte[] data;

    public PacketPayloadOutC2S() {
        super("payload_out_c2s");
    }

    @Override protected void write0(DataBuffer buffer) {
        buffer.writeBytes(data);
    }

    @Override protected void read0(DataBuffer buffer) {
        data = buffer.readBytes();
    }
}
