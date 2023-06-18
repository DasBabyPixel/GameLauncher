/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty.standalone;

import gamelauncher.engine.data.DataBuffer;
import gamelauncher.engine.network.packet.Packet;

public class PacketPayloadInS2C extends Packet {
    public byte[] data;

    public PacketPayloadInS2C() {
        super("payload_in_s2c");
    }

    public PacketPayloadInS2C(byte[] data) {
        this();
        this.data = data;
    }

    @Override protected void write0(DataBuffer buffer) {
        buffer.writeBytes(data);
    }

    @Override protected void read0(DataBuffer buffer) {
        data = buffer.readBytes();
    }
}
