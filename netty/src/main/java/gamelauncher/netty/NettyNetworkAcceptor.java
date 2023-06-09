/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import gamelauncher.engine.network.Connection;
import gamelauncher.engine.network.packet.Packet;
import gamelauncher.engine.util.logging.Logger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author DasBabyPixel
 */
public class NettyNetworkAcceptor extends SimpleChannelInboundHandler<Packet> {

    private final NettyNetworkClient client;
    private final Connection connection;
    private final Logger logger;

    public NettyNetworkAcceptor(NettyNetworkClient client, Connection connection, Logger logger) {
        this.client = client;
        this.connection = connection;
        this.logger = logger;
    }

    @Override protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
        client.handleIncomingPacket(connection, msg, logger);
    }

}
