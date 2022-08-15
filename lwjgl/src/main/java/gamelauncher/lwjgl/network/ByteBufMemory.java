package gamelauncher.lwjgl.network;

import gamelauncher.engine.network.packet.ByteMemory;
import io.netty.buffer.ByteBuf;

/**
 * @author DasBabyPixel
 */
public class ByteBufMemory extends ByteMemory {

	@SuppressWarnings("javadoc")
	public ByteBuf buf;

	@SuppressWarnings("javadoc")
	public ByteBufMemory(ByteBuf buf) {
		this.buf = buf;
	}

	@Override
	public int capacity() {
		return buf.capacity();
	}

	@Override
	public byte getByte(int index) {
		return buf.getByte(index);
	}

	@Override
	public short getShort(int index) {
		return buf.getShort(index);
	}

	@Override
	public int getInt(int index) {
		return buf.getInt(index);
	}

	@Override
	public long getLong(int index) {
		return buf.getLong(index);
	}

	@Override
	public float getFloat(int index) {
		return buf.getFloat(index);
	}

	@Override
	public double getDouble(int index) {
		return buf.getDouble(index);
	}

	@Override
	public void getBytes(int index, byte[] dst, int dstOffset, int length) {
		buf.getBytes(index, dst, dstOffset, length);
	}

	@Override
	public void setByte(int index, byte value) {
		buf.setByte(index, value);
	}

	@Override
	public void setShort(int index, short value) {
		buf.setShort(index, value);
	}

	@Override
	public void setInt(int index, int value) {
		buf.setInt(index, value);
	}

	@Override
	public void setLong(int index, long value) {
		buf.setLong(index, value);
	}

	@Override
	public void setFloat(int index, float value) {
		buf.setFloat(index, value);
	}

	@Override
	public void setDouble(int index, double value) {
		buf.setDouble(index, value);
	}

	@Override
	public void setBytes(int index, byte[] data, int dataOffset, int length) {
		buf.setBytes(index, data, dataOffset, length);
	}

}
