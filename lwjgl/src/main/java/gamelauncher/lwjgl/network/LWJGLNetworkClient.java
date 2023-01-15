package gamelauncher.lwjgl.network;

import gamelauncher.engine.network.NetworkAddress;
import gamelauncher.engine.network.NetworkClient;
import gamelauncher.engine.network.packet.Packet;
import gamelauncher.engine.network.packet.PacketEncoder;
import gamelauncher.engine.network.packet.PacketHandler;
import gamelauncher.engine.network.packet.PacketRegistry;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.SSLEngine;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LWJGLNetworkClient implements NetworkClient {

	public static final int PORT = 15684;

	private static final Logger logger = Logger.logger();

	private final Lock lock = new ReentrantLock(true);

	private final Lock handlerLock = new ReentrantLock(true);
	private final Map<Class<?>, Collection<HandlerEntry<?>>> handlers = new ConcurrentHashMap<>();
	private final PacketRegistry packetRegistry = new PacketRegistry();
	private final LWJGLNetworkHandler handler =
			new LWJGLNetworkHandler(new PacketEncoder(packetRegistry));
	private final KeyManagment keyManagment;
	private EventLoopGroup bossGroup;
	private EventLoopGroup childGroup;
	private volatile boolean running = false;
	private volatile boolean connected = false;

	public LWJGLNetworkClient(LWJGLGameLauncher launcher) {
		this.keyManagment = new KeyManagment(launcher);
	}

	@Override
	public void startClient() {
		try {
			lock.lock();
			if (running) {
				return;
			}
			bossGroup = new NioEventLoopGroup();
			childGroup = new NioEventLoopGroup();
			ServerBootstrap b = new ServerBootstrap().group(bossGroup, childGroup)
					.channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<>() {

						@Override
						protected void initChannel(@NotNull Channel ch) throws Exception {
							ChannelPipeline p = ch.pipeline();
							SslContext sslContext =
									SslContextBuilder.forServer(keyManagment.privateKey,
											keyManagment.certificate).build();
							SSLEngine engine = sslContext.newEngine(ch.alloc());
							p.addLast("ssl", new SslHandler(engine));
							p.addLast("packet_decoder", new LWJGLNetworkDecoder(handler));
							p.addLast("packet_acceptor",
									new LWJGLNetworkAcceptor(LWJGLNetworkClient.this));
							p.addLast("packet_encoder", new LWJGLNetworkEncoder(handler));
						}

					}).option(ChannelOption.SO_KEEPALIVE, true)
					.childOption(ChannelOption.SO_KEEPALIVE, true);
			Channel ch = b.bind(PORT).syncUninterruptibly().channel();
			ch.closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
				@Override
				public void operationComplete(@NotNull Future<? super Void> future)
						throws Exception {

				}
			});
			running = true;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void stopClient() {
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public boolean isServer() {
		return true;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public void connect(NetworkAddress address) {
	}

	@Override
	public void disconnect() {
	}

	@Override
	public <T extends Packet> void addHandler(Class<T> packetTpye, PacketHandler<T> handler) {
		handlerLock.lock();
		if (!handlers.containsKey(packetTpye)) {
			handlers.put(packetTpye, ConcurrentHashMap.newKeySet());
		}
		handlers.get(packetTpye).add(new HandlerEntry<>(packetTpye, handler));
		handlerLock.unlock();
	}

	@Override
	public <T extends Packet> void removeHandler(Class<T> packetType, PacketHandler<T> handler) {
		handlerLock.lock();
		if (handlers.containsKey(packetType)) {
			Collection<HandlerEntry<?>> col = handlers.get(packetType);
			col.removeIf(he -> he.clazz.equals(packetType));
			if (col.isEmpty()) {
				handlers.remove(packetType);
			}
		}
		handlerLock.unlock();
	}

	@Override
	public PacketRegistry getPacketRegistry() {
		return packetRegistry;
	}

	public void handleIncomingPacket(Packet packet) {
		Collection<HandlerEntry<?>> col = handlers.get(packet.getClass());
		if (col == null) {
			logger.info("Received unhandled packet: " + packet.getClass());
			return;
		}
		for (HandlerEntry<?> h : col) {
			h.receivePacket(packet);
		}
	}


	record HandlerEntry<T extends Packet>(Class<T> clazz, PacketHandler<T> handler) {

		public void receivePacket(Object packet) {
			handler.receivePacket(clazz.cast(packet));
		}

	}
}
