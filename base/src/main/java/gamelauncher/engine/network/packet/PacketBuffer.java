package gamelauncher.engine.network.packet;

import java.nio.charset.StandardCharsets;

/**
 * @author DasBabyPixel
 */
public class PacketBuffer {

	ByteMemory memory;

	int readerIndex = 0;

	int writerIndex = 0;

	/**
	 * @param memory
	 */
	public PacketBuffer(ByteMemory memory) {
		this.memory = memory;
	}

	/**
	 * @return the reader index
	 */
	public int readerIndex() {
		return readerIndex;
	}

	/**
	 * Sets the reader index
	 * 
	 * @param readerIndex
	 * @return the old reader index
	 */
	public int readerIndex(int readerIndex) {
		int old = this.readerIndex;
		this.readerIndex = readerIndex;
		return old;
	}

	/**
	 * @return the writer index
	 */
	public int writerIndex() {
		return writerIndex;
	}

	/**
	 * Sets the writer index
	 * 
	 * @param writerIndex
	 * @return the old writer index
	 */
	public int writerIndex(int writerIndex) {
		if (writerIndex >= this.memory.capacity()) {
			throw new ArrayIndexOutOfBoundsException(writerIndex + " >= " + this.memory.capacity());
		}
		int old = this.writerIndex;
		this.writerIndex = writerIndex;
		return old;
	}

	/**
	 * @return the number of readable bytes
	 */
	public int readableBytes() {
		return this.writerIndex - this.readerIndex;
	}

	/**
	 * @return the number of writable bytes
	 */
	public int writableBytes() {
		return this.memory.capacity() - this.writerIndex;
	}

	/**
	 * Resets the reader and writer index
	 */
	public void clear() {
		this.readerIndex = 0;
		this.writerIndex = 0;
	}

	/**
	 * Increases the {@code readerIndex} by {@code length}<br/>
	 * The same as {@link #increaseReaderIndex(int)}
	 * 
	 * @param length
	 * @return the old reader index
	 */
	public int skipBytes(int length) {
		return readerIndex(readerIndex() + length);
	}

	/**
	 * Increases the {@code readerIndex} by {@code length}
	 * 
	 * @param length
	 * @return the old reader index
	 */
	public int increaseReaderIndex(int length) {
		return readerIndex(readerIndex() + length);
	}

	/**
	 * Increases the {@code writerIndex} by {@code length}
	 * 
	 * @param length
	 * @return the old writer index
	 */
	public int increaseWriterIndex(int length) {
		return writerIndex(writerIndex() + length);
	}

	/**
	 * @param value
	 */
	public void writeByte(byte value) {
		memory.setByte(increaseWriterIndex(Byte.BYTES), value);
	}

	/**
	 * @param value
	 */
	public void writeShort(short value) {
		memory.setShort(increaseWriterIndex(Short.BYTES), value);
	}

	/**
	 * @param value
	 */
	public void writeInt(int value) {
		memory.setInt(increaseWriterIndex(Integer.BYTES), value);
	}

	/**
	 * @param value
	 */
	public void writeLong(long value) {
		memory.setLong(increaseWriterIndex(Long.BYTES), value);
	}

	/**
	 * @param value
	 */
	public void writeFloat(float value) {
		memory.setFloat(increaseWriterIndex(Float.BYTES), value);
	}

	/**
	 * @param value
	 */
	public void writeDouble(double value) {
		memory.setDouble(increaseWriterIndex(Double.BYTES), value);
	}

	/**
	 * Writes the given byte array to this {@link PacketBuffer}
	 * 
	 * @param value
	 */
	public void writeBytes(byte[] value) {
		writeInt(value.length);
		writeBytes(value, 0, value.length);
	}

	/**
	 * Writes the given byte array to this {@link PacketBuffer} from offset
	 * {@code offset} with length {@code length}
	 * 
	 * @param value
	 * @param offset
	 * @param length
	 */
	public void writeBytes(byte[] value, int offset, int length) {
		memory.setBytes(increaseWriterIndex(length), value, offset, length);
	}

	/**
	 * Writes a string to this {@link PacketBuffer}
	 * 
	 * @param string
	 */
	public void writeString(String string) {
		byte[] data = string.getBytes(StandardCharsets.UTF_8);
		writeBytes(data);
	}

	/**
	 * Writes a {@link BufferObject} at the current {@link #writerIndex()}
	 * 
	 * @param object
	 */
	public void write(BufferObject object) {
		object.write(this);
	}

	/**
	 * @return the byte at the current {@link #readerIndex()}
	 */
	public byte readByte() {
		return memory.getByte(increaseReaderIndex(Byte.BYTES));
	}

	/**
	 * @return the short at the current {@link #readerIndex()}
	 */
	public short readShort() {
		return memory.getShort(increaseReaderIndex(Short.BYTES));
	}

	/**
	 * @return the int at the current {@link #readerIndex()}
	 */
	public int readInt() {
		return memory.getInt(increaseReaderIndex(Integer.BYTES));
	}

	/**
	 * @return the long at the current {@link #readerIndex()}
	 */
	public long readLong() {
		return memory.getLong(increaseReaderIndex(Long.BYTES));
	}

	/**
	 * @return the float at the current {@link #readerIndex()}
	 */
	public float readFloat() {
		return memory.getFloat(increaseReaderIndex(Float.BYTES));
	}

	/**
	 * @return the double at the current {@link #readerIndex()}
	 */
	public double readDouble() {
		return memory.getDouble(increaseReaderIndex(Double.BYTES));
	}

	/**
	 * @return a read byte array from this {@link PacketBuffer}
	 */
	public byte[] readBytes() {
		int length = readInt();
		byte[] data = new byte[length];
		readBytes(data, 0, length);
		return data;
	}

	/**
	 * Reads a string from this {@link PacketBuffer}
	 * 
	 * @return the read string
	 */
	public String readString() {
		return new String(readBytes(), StandardCharsets.UTF_8);
	}

	/**
	 * Reads a {@link BufferObject} from the current {@link #readerIndex()}
	 * 
	 * @param object
	 */
	public void read(BufferObject object) {
		object.read(this);
	}

	/**
	 * Reads {@code length} bytes from this {@link PacketBuffer} into the specified
	 * byte array at position {@code offset}
	 * 
	 * @param value
	 * @param offset
	 * @param length
	 */
	public void readBytes(byte[] value, int offset, int length) {
		memory.getBytes(increaseReaderIndex(length), value, offset, length);
	}

	/**
	 * @return the memory of this buffer
	 */
	public ByteMemory getMemory() {
		return memory;
	}

	/**
	 * Sets the memory of this buffer
	 * 
	 * @param memory
	 */
	public void setMemory(ByteMemory memory) {
		this.memory = memory;
	}

}
