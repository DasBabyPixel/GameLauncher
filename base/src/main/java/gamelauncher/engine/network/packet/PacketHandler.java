/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.network.packet;

import gamelauncher.engine.network.Connection;

/**
 * @param <T>
 * @author DasBabyPixel
 */
public interface PacketHandler<T extends Packet> {

    void receivePacket(Connection connection, T packet);

}
