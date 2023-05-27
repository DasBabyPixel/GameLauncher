package gamelauncher.engine.network;

import gamelauncher.engine.network.packet.Packet;
import gamelauncher.engine.network.packet.PacketHandler;
import gamelauncher.engine.network.packet.PacketRegistry;
import gamelauncher.engine.resource.GameResource;
import java8.util.concurrent.CompletableFuture;

/**
 * @author DasBabyPixel
 */
public interface NetworkClient extends GameResource {

    /**
     * Starts the {@link NetworkClient}<br>
     * This is basically an init method.<br>
     * If this {@link NetworkClient} supports being a server, then the server is also started
     */
    void startClient();

    /**
     * Stops the {@link NetworkClient}<br>
     */
    void stopClient();

    /**
     * @return if the {@link NetworkClient} is running
     */
    boolean running();

    /**
     * @return true if other clients may connect to this {@link NetworkClient}
     */
    boolean server();

    /**
     * @return if this {@link NetworkClient} is connected
     */
    boolean connected();

    /**
     * Connects to the given address
     *
     * @param address
     */
    CompletableFuture<Boolean> connect(NetworkAddress address);

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
    PacketRegistry packetRegistry();

}
