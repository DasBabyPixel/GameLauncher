/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty.standalone.packet.c2s;

import gamelauncher.engine.data.DataBuffer;
import gamelauncher.engine.network.packet.Packet;

public class PacketKick extends Packet {
    public String server;
    public int target;

    public PacketKick() {
        super("kick");
    }

    public PacketKick(String server, int target) {
        this();
        this.server = server;
        this.target = target;
    }

    @Override protected void write0(DataBuffer buffer) {
        buffer.writeString(server);
        buffer.writeInt(target);
    }

    @Override protected void read0(DataBuffer buffer) {
        server = buffer.readString();
        target = buffer.readInt();
    }
}
