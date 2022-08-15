package gamelauncher.engine.network.packet;

/**
 * @author DasBabyPixel
 */
public interface BufferObject {

	/**
	 * Writes this object to the buffer
	 * 
	 * @param buffer
	 */
	void write(PacketBuffer buffer);

	/**
	 * Reads data from the buffer into this object
	 * 
	 * @param buffer
	 */
	void read(PacketBuffer buffer);

}
