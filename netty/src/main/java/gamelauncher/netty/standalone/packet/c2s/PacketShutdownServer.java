/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty.standalone.packet.c2s;

import gamelauncher.engine.data.DataBuffer;
import gamelauncher.engine.network.packet.Packet;

public class PacketShutdownServer extends Packet {
    public String id;

    public PacketShutdownServer() {
        super("shutdown_server");
    }

    public PacketShutdownServer(String id) {
        this();
        this.id = id;
    }

    @Override protected void write0(DataBuffer buffer) {
        buffer.writeString(id);
    }

    @Override protected void read0(DataBuffer buffer) {
        id = buffer.readString();
    }
}
