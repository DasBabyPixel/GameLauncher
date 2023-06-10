/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.network.packet;

import gamelauncher.engine.data.DataBuffer;

/**
 * @author DasBabyPixel
 */
public class PacketEncoder {

    private final PacketRegistry registry;

    public PacketEncoder(PacketRegistry registry) {
        this.registry = registry;
    }

    public void write(DataBuffer buffer, Packet packet) throws PacketNotRegisteredException {
        int id = packet.key().hashCode();
        registry.ensureRegistered(packet.getClass());
        buffer.writeInt(id);
        buffer.write(packet);
    }

    public Packet read(DataBuffer buffer) throws PacketNotRegisteredException {
        int id = buffer.readInt();
        Packet packet = registry.createPacket(registry.getPacketType(id));
        buffer.read(packet);
        return packet;
    }
}
