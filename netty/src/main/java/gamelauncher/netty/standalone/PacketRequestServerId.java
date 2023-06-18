/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty.standalone;

import gamelauncher.engine.data.DataBuffer;
import gamelauncher.engine.network.packet.Packet;

public class PacketRequestServerId extends Packet {
    public PacketRequestServerId() {
        super("request_server_id");
    }

    @Override protected void write0(DataBuffer buffer) {

    }

    @Override protected void read0(DataBuffer buffer) {

    }

    public static class Response extends Packet {
        public String id;

        public Response(String id) {
            this();
            this.id = id;
        }

        public Response() {
            super("request_server_id_response");
        }

        @Override protected void write0(DataBuffer buffer) {
            buffer.writeString(id);
        }

        @Override protected void read0(DataBuffer buffer) {
            id = buffer.readString();
        }
    }
}
