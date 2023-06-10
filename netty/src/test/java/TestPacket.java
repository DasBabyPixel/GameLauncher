/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

import gamelauncher.engine.data.DataBuffer;
import gamelauncher.engine.network.packet.Packet;

public class TestPacket extends Packet {
    public String msg;

    public TestPacket() {
        this(null);
    }

    public TestPacket(String msg) {
        super("test");
        this.msg = msg;
    }

    @Override protected void write0(DataBuffer buffer) {
        buffer.writeString(msg);
    }

    @Override protected void read0(DataBuffer buffer) {
        msg = buffer.readString();
    }

    @Override public String toString() {
        return "TestPacket{" + "msg='" + msg + '\'' + '}';
    }
}
