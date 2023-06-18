/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty.standalone;

import gamelauncher.engine.data.DataBuffer;
import gamelauncher.engine.network.packet.Packet;

public class PacketPayloadInC2S extends Packet {
    public int client;
    public byte[] data;

    public PacketPayloadInC2S() {
        super("payload_in_c2s");
    }

    public PacketPayloadInC2S(int client, byte[] data) {
        this();
        this.client = client;
        this.data = data;
    }

    @Override protected void write0(DataBuffer buffer) {
        buffer.writeInt(client);
        buffer.writeBytes(data);
    }

    @Override protected void read0(DataBuffer buffer) {
        client = buffer.readInt();
        data = buffer.readBytes();
    }
}
