package gamelauncher.engine.network.packet;

/**
 * @author DasBabyPixel
 */
public interface BufferObject {

    /**
     * Writes this object to the buffer
     */
    void write(PacketBuffer buffer);

    /**
     * Reads data from the buffer into this object
     */
    void read(PacketBuffer buffer);

}
