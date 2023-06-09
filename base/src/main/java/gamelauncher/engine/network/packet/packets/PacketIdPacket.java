/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.network.packet.packets;

import gamelauncher.engine.data.DataBuffer;
import gamelauncher.engine.network.packet.Packet;

@Deprecated(forRemoval = true)
public class PacketIdPacket extends Packet {

    public int id;

    public PacketIdPacket() {
        super("packet_id");
    }

    public PacketIdPacket(int id) {
        this();
        this.id = id;
    }

    @Override protected void write0(DataBuffer buffer) {
        buffer.writeInt(id);
    }

    @Override protected void read0(DataBuffer buffer) {
        id = buffer.readInt();
    }

    @Override public String toString() {
        return "PacketIdPacket{" + "id=" + id + '}';
    }
}
