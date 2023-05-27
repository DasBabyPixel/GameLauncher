package gamelauncher.netty;

import gamelauncher.engine.network.packet.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author DasBabyPixel
 */
public class NettyNetworkAcceptor extends SimpleChannelInboundHandler<Packet> {

    private final NettyNetworkClient client;

    public NettyNetworkAcceptor(NettyNetworkClient client) {
        this.client = client;
    }

    @Override protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
        client.handleIncomingPacket(msg);
    }

}
