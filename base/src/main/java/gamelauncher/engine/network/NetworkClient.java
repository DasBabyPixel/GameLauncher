package gamelauncher.engine.network;

import gamelauncher.engine.network.packet.Packet;
import gamelauncher.engine.network.packet.PacketHandler;
import gamelauncher.engine.network.packet.PacketRegistry;

/**
 * @author DasBabyPixel
 */
public interface NetworkClient {

	/**
	 * Starts the {@link NetworkClient}
	 */
	void startClient();

	/**
	 * Stops the {@link NetworkClient}
	 */
	void stopClient();

	/**
	 * @return if the {@link NetworkClient} is running
	 */
	boolean isRunning();

	/**
	 * @return true if other clients may connect to this {@link NetworkClient}
	 */
	boolean isServer();

	/**
	 * @return if this {@link NetworkClient} is connected
	 */
	boolean isConnected();

	/**
	 * Connects to the given address
	 * 
	 * @param address
	 */
	void connect(NetworkAddress address);

	/**
	 * Disconnects this {@link NetworkClient} from all current connections
	 */
	void disconnect();

	/**
	 * @param <T>
	 * @param packetTpye
	 * @param handler
	 */
	<T extends Packet> void addHandler(Class<T> packetTpye, PacketHandler<T> handler);

	/**
	 * @param <T>
	 * @param packetTpye
	 * @param handler
	 */
	<T extends Packet> void removeHandler(Class<T> packetTpye, PacketHandler<T> handler);

	/**
	 * @return the {@link PacketRegistry}
	 */
	PacketRegistry getPacketRegistry();

}
