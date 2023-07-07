/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.network.packet;

import gamelauncher.engine.network.Connection;
import org.jetbrains.annotations.NotNull;

/**
 * @param <T>
 * @author DasBabyPixel
 */
public interface PacketHandler<T extends Packet> {

    void receivePacket(@NotNull Connection connection, @NotNull T packet);

}
