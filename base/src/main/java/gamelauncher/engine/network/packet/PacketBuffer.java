package gamelauncher.engine.network.packet;

import de.dasbabypixel.annotations.Api;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author DasBabyPixel
 */
@Api
public class PacketBuffer {

    private ByteMemory memory;
    private int readerIndex = 0;
    private int writerIndex = 0;

    public PacketBuffer(ByteMemory memory) {
        this.memory = memory;
    }

    /**
     * @return the reader index
     */
    @Api public int readerIndex() {
        return readerIndex;
    }

    /**
     * Sets the reader index
     *
     * @return the old reader index
     */
    @Api public int readerIndex(int readerIndex) {
        int old = this.readerIndex;
        this.readerIndex = readerIndex;
        return old;
    }

    /**
     * @return the writer index
     */
    @Api public int writerIndex() {
        return writerIndex;
    }

    /**
     * Sets the writer index
     *
     * @return the old writer index
     */
    @Api public int writerIndex(int writerIndex) {
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
    @Api public int readableBytes() {
        return this.writerIndex - this.readerIndex;
    }

    /**
     * @return the number of writable bytes
     */
    @Api public int writableBytes() {
        return this.memory.capacity() - this.writerIndex;
    }

    /**
     * Resets the reader and writer index
     */
    @Api public void clear() {
        this.readerIndex = 0;
        this.writerIndex = 0;
    }

    /**
     * Increases the {@code readerIndex} by {@code length}<br>
     * The same as {@link #increaseReaderIndex(int)}
     *
     * @return the old reader index
     */
    @Api public int skipBytes(int length) {
        return readerIndex(readerIndex() + length);
    }

    /**
     * Increases the {@code readerIndex} by {@code length}
     *
     * @return the old reader index
     */
    @Api public int increaseReaderIndex(int length) {
        return readerIndex(readerIndex() + length);
    }

    /**
     * Increases the {@code writerIndex} by {@code length}
     *
     * @return the old writer index
     */
    @Api public int increaseWriterIndex(int length) {
        return writerIndex(writerIndex() + length);
    }

    @Api public void writeByte(byte value) {
        memory.setByte(increaseWriterIndex(Byte.BYTES), value);
    }

    @Api public void writeShort(short value) {
        memory.setShort(increaseWriterIndex(Short.BYTES), value);
    }

    @Api public void writeList(List<? extends BufferObject> list) {
        writeInt(list.size());
        for (BufferObject object : list) {
            write(object);
        }
    }

    @Api public <T extends BufferObject> void readList(List<T> list, Supplier<T> instanceCreator) {
        list.clear();
        int size = readInt();
        for (int i = 0; i < size; i++) {
            T o = instanceCreator.get();
            read(o);
            list.add(o);
        }
    }

    @Api public void writeInt(int value) {
        memory.setInt(increaseWriterIndex(Integer.BYTES), value);
    }

    @Api public void writeLong(long value) {
        memory.setLong(increaseWriterIndex(Long.BYTES), value);
    }

    @Api public void writeFloat(float value) {
        memory.setFloat(increaseWriterIndex(Float.BYTES), value);
    }

    @Api public void writeDouble(double value) {
        memory.setDouble(increaseWriterIndex(Double.BYTES), value);
    }

    /**
     * Writes the given byte array to this {@link PacketBuffer}
     */
    @Api public void writeBytes(byte[] value) {
        writeInt(value.length);
        writeBytes(value, 0, value.length);
    }

    /**
     * Writes the given byte array to this {@link PacketBuffer} from offset
     * {@code offset} with length {@code length}
     */
    @Api public void writeBytes(byte[] value, int offset, int length) {
        memory.setBytes(increaseWriterIndex(length), value, offset, length);
    }

    /**
     * Writes a string to this {@link PacketBuffer}
     */
    @Api public void writeString(String string) {
        byte[] data = string.getBytes(StandardCharsets.UTF_8);
        writeBytes(data);
    }

    /**
     * Writes a {@link BufferObject} at the current {@link #writerIndex()}
     */
    @Api public void write(BufferObject object) {
        object.write(this);
    }

    /**
     * @return the byte at the current {@link #readerIndex()}
     */
    @Api public byte readByte() {
        return memory.getByte(increaseReaderIndex(Byte.BYTES));
    }

    /**
     * @return the short at the current {@link #readerIndex()}
     */
    @Api public short readShort() {
        return memory.getShort(increaseReaderIndex(Short.BYTES));
    }

    /**
     * @return the int at the current {@link #readerIndex()}
     */
    @Api public int readInt() {
        return memory.getInt(increaseReaderIndex(Integer.BYTES));
    }

    /**
     * @return the long at the current {@link #readerIndex()}
     */
    @Api public long readLong() {
        return memory.getLong(increaseReaderIndex(Long.BYTES));
    }

    /**
     * @return the float at the current {@link #readerIndex()}
     */
    @Api public float readFloat() {
        return memory.getFloat(increaseReaderIndex(Float.BYTES));
    }

    /**
     * @return the double at the current {@link #readerIndex()}
     */
    @Api public double readDouble() {
        return memory.getDouble(increaseReaderIndex(Double.BYTES));
    }

    /**
     * @return a read byte array from this {@link PacketBuffer}
     */
    @Api public byte[] readBytes() {
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
    @Api public String readString() {
        return new String(readBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Reads a {@link BufferObject} from the current {@link #readerIndex()}
     */
    @Api public void read(BufferObject object) {
        object.read(this);
    }

    /**
     * Reads {@code length} bytes from this {@link PacketBuffer} into the specified
     * byte array at position {@code offset}
     */
    @Api public void readBytes(byte[] value, int offset, int length) {
        memory.getBytes(increaseReaderIndex(length), value, offset, length);
    }

    /**
     * @return the memory of this buffer
     */
    @Api public ByteMemory getMemory() {
        return memory;
    }

    /**
     * Sets the memory of this buffer
     */
    @Api public void setMemory(ByteMemory memory) {
        this.memory = memory;
    }

}
