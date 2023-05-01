/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.data;

import de.dasbabypixel.annotations.Api;
import org.joml.Math;

public class DataMemory extends ByteMemory {
    private static final int increment = 256;
    private byte[] array;
    private int capacity;

    @Api public DataMemory() {
        this(new byte[increment]);
    }

    @Api public DataMemory(byte[] array) {
        this.array = array;
    }

    @Override public int capacity() {
        return capacity;
    }

    @Override public void capacity(int capacity) throws UnsupportedOperationException {
        if (this.capacity == capacity) return;
        this.capacity = capacity;
        byte[] narray = new byte[capacity];
        System.arraycopy(array, 0, narray, 0, Math.min(narray.length, array.length));
        array = narray;
    }

    private void ensureCapacity(int capacity) {
        if (this.capacity >= capacity) return;
        int diff = capacity - this.capacity;
        if (diff < increment) {
            capacity = this.capacity + increment;
        }
        capacity(capacity);
    }

    @Override public byte getByte(int index) {
        return DataUtil.getByte(array, index);
    }

    @Override public short getShort(int index) {
        return DataUtil.getShort(array, index);
    }

    @Override public int getInt(int index) {
        return DataUtil.getInt(array, index);
    }

    @Override public long getLong(int index) {
        return DataUtil.getLong(array, index);
    }

    @Override public float getFloat(int index) {
        return Float.intBitsToFloat(getInt(index));
    }

    @Override public double getDouble(int index) {
        return Double.longBitsToDouble(getLong(index));
    }

    @Override public void getBytes(int index, byte[] dst, int dstOffset, int length) {
        for (int i = dstOffset, j = index, k = 0; k < length; i++, j++, k++) dst[i] = array[j];
    }

    @Override public void setByte(int index, byte value) {
        ensureCapacity(index + DataUtil.BYTES_BYTE);
        DataUtil.setByte(array, index, value);
    }

    @Override public void setShort(int index, short value) {
        ensureCapacity(index + DataUtil.BYTES_SHORT);
        DataUtil.setShortLE(array, index, value);
    }

    @Override public void setInt(int index, int value) {
        ensureCapacity(index + DataUtil.BYTES_INT);
        DataUtil.setInt(array, index, value);
    }

    @Override public void setLong(int index, long value) {
        ensureCapacity(index + DataUtil.BYTES_LONG);
        DataUtil.setLong(array, index, value);
    }

    @Override public void setFloat(int index, float value) {
        ensureCapacity(index + DataUtil.BYTES_FLOAT);
        DataUtil.setInt(array, index, Float.floatToRawIntBits(value));
    }

    @Override public void setDouble(int index, double value) {
        ensureCapacity(index + DataUtil.BYTES_DOUBLE);
        DataUtil.setLong(array, index, Double.doubleToRawLongBits(value));
    }

    @Override public void setBytes(int index, byte[] data, int dataOffset, int length) {
        for (int i = dataOffset, j = index, k = 0; k < length; i++, j++, k++) array[j] = data[i];
    }

    public byte[] array() {
        return array;
    }
}
