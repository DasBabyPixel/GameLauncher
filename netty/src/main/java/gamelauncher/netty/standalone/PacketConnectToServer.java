/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty.standalone;

import gamelauncher.engine.data.DataBuffer;
import gamelauncher.engine.network.packet.Packet;

public class PacketConnectToServer extends Packet {
    public String id;

    public PacketConnectToServer(String id) {
        this();
        this.id = id;
    }

    public PacketConnectToServer() {
        super("connect_to_server");
    }

    @Override protected void write0(DataBuffer buffer) {
        buffer.writeString(id);
    }

    @Override protected void read0(DataBuffer buffer) {
        id = buffer.readString();
    }
}
