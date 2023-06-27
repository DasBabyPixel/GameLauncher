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
import org.jetbrains.annotations.NotNull;

/**
 * @author DasBabyPixel
 */
public class NettyNetworkAcceptor extends SimpleChannelInboundHandler<Packet> {

    private final NettyNetworkClient client;
    private final AbstractConnection connection;
    private final Logger logger;

    public NettyNetworkAcceptor(NettyNetworkClient client, AbstractConnection connection, Logger logger) {
        this.client = client;
        this.connection = connection;
        this.logger = logger;
    }

    @Override public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
        if (connection.state.value() == Connection.State.CONNECTED) {
            connection.state.value(Connection.State.CLOSED);
            NettyNetworkClient.logger.infof("Disconnected from %s", ctx.channel().remoteAddress());
        }
        super.channelInactive(ctx);
    }

    @Override protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
        client.handleIncomingPacket(connection, msg, logger);
    }

}
