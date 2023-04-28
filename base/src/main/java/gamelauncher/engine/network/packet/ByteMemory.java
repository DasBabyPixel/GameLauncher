package gamelauncher.engine.network.packet;

/**
 * @author DasBabyPixel
 */
public abstract class ByteMemory {

    /**
     * @return the number of bytes this memory can contain
     */
    public abstract int capacity();

    /**
     * @return the byte at the given index
     */
    public abstract byte getByte(int index);

    /**
     * @return the short at the given index
     */
    public abstract short getShort(int index);

    /**
     * @return the int at the given index
     */
    public abstract int getInt(int index);

    /**
     * @return the long at the given index
     */
    public abstract long getLong(int index);

    /**
     * @return the float at the given index
     */
    public abstract float getFloat(int index);

    /**
     * @return the double at the given index
     */
    public abstract double getDouble(int index);

    /**
     * Reads {@code length} bytes into {@code dst} with offset {@code dstOffset}
     * from index {@code index}
     */
    public abstract void getBytes(int index, byte[] dst, int dstOffset, int length);

    /**
     * Sets the byte at the given index
     */
    public abstract void setByte(int index, byte value);

    /**
     * Sets the short at the given index
     */
    public abstract void setShort(int index, short value);

    /**
     * Sets the int at the given index
     */
    public abstract void setInt(int index, int value);

    /**
     * Sets the long at the given index
     */
    public abstract void setLong(int index, long value);

    /**
     * Sets the float at the given index
     */
    public abstract void setFloat(int index, float value);

    /**
     * Sets the double at the given index
     */
    public abstract void setDouble(int index, double value);

    /**
     * Writes {@code length} bytes from {@code data} offset by {@code dataOffset} at
     * index {@code index}
     */
    public abstract void setBytes(int index, byte[] data, int dataOffset, int length);

}
