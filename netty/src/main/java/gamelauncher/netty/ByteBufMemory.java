/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import gamelauncher.engine.data.ByteMemory;
import gamelauncher.engine.data.DataUtil;
import io.netty.buffer.ByteBuf;

/**
 * @author DasBabyPixel
 */
public class ByteBufMemory extends ByteMemory {

    public ByteBuf buf;

    public ByteBufMemory() {
        this(null);
    }

    public ByteBufMemory(ByteBuf buf) {
        this.buf = buf;
    }

    @Override public int capacity() {
        return buf.capacity();
    }

    @Override public void capacity(int capacity) throws UnsupportedOperationException {
        buf.capacity(capacity);
    }

    @Override public byte getByte(int index) {
        return buf.getByte(index);
    }

    @Override public short getShort(int index) {
        return buf.getShort(index);
    }

    @Override public int getInt(int index) {
        return buf.getInt(index);
    }

    @Override public long getLong(int index) {
        return buf.getLong(index);
    }

    @Override public float getFloat(int index) {
        return buf.getFloat(index);
    }

    @Override public double getDouble(int index) {
        return buf.getDouble(index);
    }

    @Override public void getBytes(int index, byte[] dst, int dstOffset, int length) {
        buf.getBytes(index, dst, dstOffset, length);
    }

    @Override public void setByte(int index, byte value) {
        ensureCapacity(index + DataUtil.BYTES_BYTE);
        buf.setByte(index, value);
    }

    @Override public void setShort(int index, short value) {
        ensureCapacity(index + DataUtil.BYTES_SHORT);
        buf.setShort(index, value);
    }

    @Override public void setInt(int index, int value) {
        ensureCapacity(index + DataUtil.BYTES_INT);
        buf.setInt(index, value);
    }

    @Override public void setLong(int index, long value) {
        ensureCapacity(index + DataUtil.BYTES_LONG);
        buf.setLong(index, value);
    }

    @Override public void setFloat(int index, float value) {
        ensureCapacity(index + DataUtil.BYTES_FLOAT);
        buf.setFloat(index, value);
    }

    @Override public void setDouble(int index, double value) {
        ensureCapacity(index + DataUtil.BYTES_DOUBLE);
        buf.setDouble(index, value);
    }

    @Override public void setBytes(int index, byte[] data, int dataOffset, int length) {
        ensureCapacity(index + length);
        buf.setBytes(index, data, dataOffset, length);
    }

    private void ensureCapacity(int c) {
        int cap = buf.capacity();
        int nc = cap;
        while (nc < c) nc = nc * 2;
        if (nc != cap) buf.capacity(nc);
    }
}
