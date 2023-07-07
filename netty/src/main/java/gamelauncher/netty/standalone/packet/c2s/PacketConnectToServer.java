/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty.standalone.packet.c2s;

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

    public static class Response extends Packet {
        public static final int SUCCESS = 0;
        public static final int FAILURE = 1;
        public int code;

        public Response() {
            super("connect_to_server_response");
        }

        public Response(int code) {
            this();
            this.code = code;
        }

        @Override protected void write0(DataBuffer buffer) {
            buffer.writeInt(code);
        }

        @Override protected void read0(DataBuffer buffer) {
            code = buffer.readInt();
        }
    }
}
