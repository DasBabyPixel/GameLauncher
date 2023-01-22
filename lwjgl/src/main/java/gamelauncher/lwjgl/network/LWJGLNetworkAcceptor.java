package gamelauncher.lwjgl.network;

import gamelauncher.engine.network.packet.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author DasBabyPixel
 */
public class LWJGLNetworkAcceptor extends SimpleChannelInboundHandler<Packet> {

	private final LWJGLNetworkClient client;

	public LWJGLNetworkAcceptor(LWJGLNetworkClient client) {
		this.client = client;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
		client.handleIncomingPacket(msg);
	}

}
