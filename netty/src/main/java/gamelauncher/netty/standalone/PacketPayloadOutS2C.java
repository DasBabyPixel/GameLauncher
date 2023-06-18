/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty.standalone;

import gamelauncher.engine.data.DataBuffer;
import gamelauncher.engine.network.packet.Packet;

public class PacketPayloadOutS2C extends Packet {
    public String serverId;
    public int target;
    public byte[] data;

    public PacketPayloadOutS2C() {
        super("payload_out_s2c");
    }

    public PacketPayloadOutS2C(String serverId, int target, byte[] data) {
        this();
        this.serverId = serverId;
        this.target = target;
        this.data = data;
    }

    @Override protected void write0(DataBuffer buffer) {
        buffer.writeString(serverId);
        buffer.writeInt(target);
        buffer.writeBytes(data);
    }

    @Override protected void read0(DataBuffer buffer) {
        serverId = buffer.readString();
        target = buffer.readInt();
        data = buffer.readBytes();
    }
}
