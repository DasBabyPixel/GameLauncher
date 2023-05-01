/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.data;

import de.dasbabypixel.annotations.Api;

/**
 * All implementations should automatically try to increase the capacity if needed!
 *
 * @author DasBabyPixel
 */
@Api
public abstract class ByteMemory {

    /**
     * @return the number of bytes this memory can contain
     */
    @Api public abstract int capacity();

    /**
     * Tries to set the capacity for this memory.
     *
     * @param capacity the new capacity
     * @throws UnsupportedOperationException if the capacity can't be changed
     */
    @Api public abstract void capacity(int capacity) throws UnsupportedOperationException;

    /**
     * @return the byte at the given index
     */
    @Api public abstract byte getByte(int index);

    /**
     * @return the short at the given index
     */
    @Api public abstract short getShort(int index);

    /**
     * @return the int at the given index
     */
    @Api public abstract int getInt(int index);

    /**
     * @return the long at the given index
     */
    @Api public abstract long getLong(int index);

    /**
     * @return the float at the given index
     */
    @Api public abstract float getFloat(int index);

    /**
     * @return the double at the given index
     */
    @Api public abstract double getDouble(int index);

    /**
     * Reads {@code length} bytes into {@code dst} with offset {@code dstOffset}
     * from index {@code index}
     */
    @Api public abstract void getBytes(int index, byte[] dst, int dstOffset, int length);

    /**
     * Sets the byte at the given index
     */
    @Api public abstract void setByte(int index, byte value);

    /**
     * Sets the short at the given index
     */
    @Api public abstract void setShort(int index, short value);

    /**
     * Sets the int at the given index
     */
    @Api public abstract void setInt(int index, int value);

    /**
     * Sets the long at the given index
     */
    @Api public abstract void setLong(int index, long value);

    /**
     * Sets the float at the given index
     */
    @Api public abstract void setFloat(int index, float value);

    /**
     * Sets the double at the given index
     */
    @Api public abstract void setDouble(int index, double value);

    /**
     * Writes {@code length} bytes from {@code data} offset by {@code dataOffset} at
     * index {@code index}
     */
    @Api public abstract void setBytes(int index, byte[] data, int dataOffset, int length);

}
