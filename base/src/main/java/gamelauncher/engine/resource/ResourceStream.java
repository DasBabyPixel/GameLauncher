package gamelauncher.engine.resource;

import com.google.common.base.Charsets;
import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameFunction;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author DasBabyPixel
 */
public class ResourceStream extends AbstractGameResource implements Closeable {

    private static final int NULL = -1;
    private final Path path;
    private final boolean directory;
    private final Lock bufLock = new ReentrantLock(true);
    private final ByteBuffer buf = ByteBuffer.allocate(4);
    /**
     * The last error generated by {@link #close()} or {@link #cleanup()}
     */
    public GameException closeError = null;
    private InputStream in;
    private OutputStream out;

    public ResourceStream(Path path, boolean directory, InputStream in, OutputStream out) {
        this.path = path;
        this.directory = directory;
        this.in = in;
        this.out = out;
    }

    /**
     * @return if this {@link ResourceStream} has an {@link InputStream}
     */
    public boolean hasInputStream() {
        return this.in != null;
    }

    /**
     * @return the {@link InputStream} of this {@link ResourceStream}
     */
    public InputStream getInputStream() {
        return this.in;
    }

    /**
     * @return if this {@link ResourceStream} has an {@link OutputStream}
     */
    public boolean hasOutputStream() {
        return this.out != null;
    }

    /**
     * @return the {@link OutputStream} of this {@link ResourceStream}
     */
    public OutputStream getOutputStream() {
        return this.out;
    }

    /**
     * @param n the number of bytes to skip
     * @throws GameException an exception
     * @see InputStream#skip(long)
     */
    public void skip(long n) throws GameException {
        if (this.in != null) {
            try {
                this.in.skip(n);
            } catch (IOException ex) {
                throw new GameException(ex);
            }
        }
    }

    @Override public void close() throws IOException {
        try {
            this.cleanup();
        } catch (GameException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * @throws GameException an exception
     * @see InputStream#close()
     * @see OutputStream#close()
     */
    @Override protected void cleanup0() throws GameException {
        try {
            if (this.in != null) {
                this.in.close();
                this.in = null;
            }
            if (this.out != null) {
                this.out.close();
                this.out = null;
            }
        } catch (IOException ex) {
            throw this.closeError = new GameException(ex);
        }
    }

    /**
     * @param length the length of the string to read
     * @return the string
     * @throws GameException an exception
     */
    public String readUTF8(int length) throws GameException {
        return new String(this.readBytes(length), Charsets.UTF_8);
    }

    /**
     * @param string the string to write
     * @throws GameException an exception
     */
    public void writeUTF8(String string) throws GameException {
        this.writeBytes(string.getBytes(Charsets.UTF_8));
    }

    /**
     * @return the string
     * @throws GameException an exception
     */
    public String readUTF8Fully() throws GameException {
        return new String(this.readAllBytes(), Charsets.UTF_8);
    }

    /**
     * @param string the string to write
     * @throws GameException an exception
     */
    public void swriteUTF8(String string) throws GameException {
        if (string == null) {
            this.swriteBytes(null);
        } else {
            this.swriteBytes(string.getBytes(Charsets.UTF_8));
        }
    }

    /**
     * @return the string
     * @throws GameException an exception
     */
    public String sreadUTF8() throws GameException {
        byte[] b = this.sreadBytes();
        if (b == null) return null;
        return new String(b, Charsets.UTF_8);
    }

    public void swriteFloats(float[] floats) throws GameException {
        if (floats == null) {
            this.writeInt(ResourceStream.NULL);
        } else {
            this.writeInt(floats.length);
            this.writeFloats(floats);
        }
    }

    /**
     * @return the floats
     */
    public float[] sreadFloats() throws GameException {
        int len = this.readInt();
        if (len == ResourceStream.NULL) return null;
        return this.readFloats(len);
    }

    public void swriteInts(int[] ints) throws GameException {
        if (ints == null) {
            this.writeInt(ResourceStream.NULL);
        } else {
            this.writeInt(ints.length);
            this.writeInts(ints);
        }
    }

    /**
     * @return the ints
     */
    public int[] sreadInts() throws GameException {
        int len = this.readInt();
        if (len == ResourceStream.NULL) return null;
        return this.readInts(len);
    }

    public void swriteBytes(byte[] bytes) throws GameException {
        if (bytes == null) {
            this.writeInt(ResourceStream.NULL);
        } else {
            this.writeInt(bytes.length);
            this.writeBytes(bytes);
        }
    }

    /**
     * @return the bytes
     */
    public byte[] sreadBytes() throws GameException {
        int len = this.readInt();
        if (len == ResourceStream.NULL) return null;
        return this.readBytes(len);
    }

    /**
     * @return the string
     */
    public String readUTF8FullyClose() throws GameException {
        String utf8 = this.readUTF8Fully();
        this.cleanup();
        return utf8;
    }

    /**
     * @return the size
     */
    public int readBytes(byte[] bytes) throws GameException {
        try {
            return this.in.read(bytes);
        } catch (IOException ex) {
            throw new GameException(ex);
        }
    }

    public byte[] readAllBytes() throws GameException {
        byte[] buffer = new byte[2048];
        ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
        int read;
        while ((read = this.readBytes(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }

    public byte readByte() throws GameException {
        try {
            int read = this.in.read();
            if (read == -1) {
                throw new GameException("End of stream");
            }
            return (byte) read;
        } catch (IOException ex) {
            throw new GameException(ex);
        }
    }

    public void writeByte(byte b) throws GameException {
        try {
            this.out.write(b);
        } catch (IOException ex) {
            throw new GameException(ex);
        }
    }

    public void writeBytes(byte[] b) throws GameException {
        try {
            this.out.write(b);
        } catch (IOException ex) {
            throw new GameException(ex);
        }
    }

    public int readInt() throws GameException {
        return this.byteBuffer(buf -> {
            buf.put(this.readBytes(4));
            buf.rewind();
            return buf.getInt();
        });
    }

    public void writeInt(int i) throws GameException {
        this.byteBuffer(buf -> {
            buf.putInt(i);
            buf.rewind();
            this.writeBytes(buf.array());
            return null;
        });
    }

    public float readFloat() throws GameException {
        return this.byteBuffer(buf -> {
            buf.put(this.readBytes(4));
            buf.rewind();
            return buf.getFloat();
        });
    }

    public void writeFloat(float f) throws GameException {
        this.byteBuffer(buf -> {
            buf.putFloat(f);
            buf.rewind();
            this.writeBytes(buf.array());
            return null;
        });
    }

    public void writeFloats(float[] floats) throws GameException {
        for (float f : floats) {
            this.writeFloat(f);
        }
    }

    /**
     * @return the floats
     */
    public float[] readFloats(int length) throws GameException {
        float[] floats = new float[length];
        for (int i = 0; i < floats.length; i++) {
            floats[i] = this.readFloat();
        }
        return floats;
    }

    public void writeInts(int[] ints) throws GameException {
        for (int f : ints) {
            this.writeInt(f);
        }
    }

    /**
     * @return the ints
     */
    public int[] readInts(int length) throws GameException {
        int[] ints = new int[length];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = this.readInt();
        }
        return ints;
    }

    private <T> T byteBuffer(GameFunction<ByteBuffer, T> function) throws GameException {
        this.bufLock.lock();
        this.buf.rewind();
        T t = function.apply(this.buf);
        this.bufLock.unlock();
        return t;
    }

    /**
     * @return the bytes
     */
    public byte[] readBytes(int length) throws GameException {
        byte[] bytes = new byte[length];
        length = this.readBytes(bytes);
        return Arrays.copyOf(bytes, length);
    }

    /**
     * @return if this {@link ResourceStream} is a directory
     */
    @Api public boolean isDirectory() {
        return this.directory;
    }

    /**
     * @return the path
     */
    public Path getPath() {
        return this.path;
    }

}
