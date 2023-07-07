/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty.standalone.packet.s2c;

import gamelauncher.engine.data.DataBuffer;
import gamelauncher.engine.network.packet.Packet;

public class PacketKicked extends Packet {
    public String message;

    public PacketKicked() {
        super("kicked");
    }

    public PacketKicked(String message) {
        this();
        this.message = message;
    }

    @Override protected void write0(DataBuffer buffer) {
        buffer.writeString(message);
    }

    @Override protected void read0(DataBuffer buffer) {
        message = buffer.readString();
    }
}
