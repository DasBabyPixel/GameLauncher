/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty.standalone.packet.c2s;

import gamelauncher.engine.data.DataBuffer;
import gamelauncher.engine.network.packet.Packet;

public class PacketPayloadOutS2A extends Packet {
    public String serverId;
    public byte[] data;

    public PacketPayloadOutS2A() {
        super("payload_out_s2a");
    }

    public PacketPayloadOutS2A(String serverId, byte[] data) {
        this();
        this.serverId = serverId;
        this.data = data;
    }

    @Override protected void write0(DataBuffer buffer) {
        buffer.writeString(serverId);
        buffer.writeBytes(data);
    }

    @Override protected void read0(DataBuffer buffer) {
        serverId = buffer.readString();
        data = buffer.readBytes();
    }
}
