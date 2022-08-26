package gamelauncher.engine.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.matthiasmann.twl.utils.PNGDecoder;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameFunction;

/**
 * @author DasBabyPixel
 */
public class ResourceStream extends AbstractGameResource implements AutoCloseable {

	private final Path path;

	private final boolean directory;

	private InputStream in;

	private OutputStream out;

	private final Lock bufLock = new ReentrantLock(true);

	private final ByteBuffer buf = ByteBuffer.allocate(4);

	private static final int NULL = -1;

	/**
	 * @param path
	 * @param directory
	 * @param in
	 * @param out
	 */
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
		return in != null;
	}

	/**
	 * @return the {@link InputStream} of this {@link ResourceStream}
	 */
	public InputStream getInputStream() {
		return in;
	}

	/**
	 * @return if this {@link ResourceStream} has an {@link OutputStream}
	 */
	public boolean hasOutputStream() {
		return out != null;
	}

	/**
	 * @return the {@link OutputStream} of this {@link ResourceStream}
	 */
	public OutputStream getOutputStream() {
		return out;
	}

	/**
	 * @see InputStream#skip(long)
	 * @param n
	 * @throws GameException
	 */
	public void skip(long n) throws GameException {
		if (in != null) {
			try {
				in.skip(n);
			} catch (IOException ex) {
				throw new GameException(ex);
			}
		}
	}

	@Override
	public void close() throws IOException {
		try {
			cleanup();
		} catch (GameException ex) {
			throw new IOException(ex);
		}
	}

	/**
	 * @see InputStream#close()
	 * @see OutputStream#close()
	 * @throws GameException
	 */
	@Override
	public void cleanup0() throws GameException {
		try {
			if (in != null) {
				in.close();
				in = null;
			}
			if (out != null) {
				out.close();
				out = null;
			}
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

	/**
	 * @return a new {@link PNGDecoder} for this {@link ResourceStream}
	 * @throws GameException
	 */
	@Deprecated
	public PNGDecoder newPNGDecoder() throws GameException {
		try {
			return new PNGDecoder(in);
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

	/**
	 * @param length
	 * @return the string
	 * @throws GameException
	 */
	public String readUTF8(int length) throws GameException {
		return new String(readBytes(length), StandardCharsets.UTF_8);
	}

	/**
	 * @param string
	 * @throws GameException
	 */
	public void writeUTF8(String string) throws GameException {
		writeBytes(string.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * @return the string
	 * @throws GameException
	 */
	public String readUTF8Fully() throws GameException {
		return new String(readAllBytes(), StandardCharsets.UTF_8);
	}

	/**
	 * @param string
	 * @throws GameException
	 */
	public void swriteUTF8(String string) throws GameException {
		if (string == null) {
			swriteBytes(null);
		} else {
			swriteBytes(string.getBytes(StandardCharsets.UTF_8));
		}
	}

	/**
	 * @return the string
	 * @throws GameException
	 */
	public String sreadUTF8() throws GameException {
		byte[] b = sreadBytes();
		if (b == null)
			return null;
		return new String(b, StandardCharsets.UTF_8);
	}

	/**
	 * @param floats
	 * @throws GameException
	 */
	public void swriteFloats(float[] floats) throws GameException {
		if (floats == null) {
			writeInt(NULL);
		} else {
			writeInt(floats.length);
			writeFloats(floats);
		}
	}

	/**
	 * @return the floats
	 * @throws GameException
	 */
	public float[] sreadFloats() throws GameException {
		int len = readInt();
		if (len == NULL)
			return null;
		return readFloats(len);
	}

	/**
	 * @param ints
	 * @throws GameException
	 */
	public void swriteInts(int[] ints) throws GameException {
		if (ints == null) {
			writeInt(NULL);
		} else {
			writeInt(ints.length);
			writeInts(ints);
		}
	}

	/**
	 * @return the ints
	 * @throws GameException
	 */
	public int[] sreadInts() throws GameException {
		int len = readInt();
		if (len == NULL)
			return null;
		return readInts(len);
	}

	/**
	 * @param bytes
	 * @throws GameException
	 */
	public void swriteBytes(byte[] bytes) throws GameException {
		if (bytes == null) {
			writeInt(NULL);
		} else {
			writeInt(bytes.length);
			writeBytes(bytes);
		}
	}

	/**
	 * @return the bytes
	 * @throws GameException
	 */
	public byte[] sreadBytes() throws GameException {
		int len = readInt();
		if (len == NULL)
			return null;
		return readBytes(len);
	}

	/**
	 * @return the string
	 * @throws GameException
	 */
	public String readUTF8FullyClose() throws GameException {
		String utf8 = readUTF8Fully();
		cleanup();
		return utf8;
	}

	/**
	 * @param bytes
	 * @return the size
	 * @throws GameException
	 */
	public int readBytes(byte[] bytes) throws GameException {
		try {
			return in.read(bytes);
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

	/**
	 * @return the bytes
	 * @throws GameException
	 */
	public byte[] readAllBytes() throws GameException {
		byte[] buffer = new byte[2048];
		ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
		int read;
		while ((read = readBytes(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
		return out.toByteArray();
	}

	/**
	 * @return the byte
	 * @throws GameException
	 */
	public byte readByte() throws GameException {
		try {
			int read = in.read();
			if (read == -1) {
				throw new GameException("End of stream");
			}
			return (byte) read;
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

	/**
	 * @param b
	 * @throws GameException
	 */
	public void writeByte(byte b) throws GameException {
		try {
			out.write(b);
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

	/**
	 * @param b
	 * @throws GameException
	 */
	public void writeBytes(byte[] b) throws GameException {
		try {
			out.write(b);
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

	/**
	 * @return the int
	 * @throws GameException
	 */
	public int readInt() throws GameException {
		return byteBuffer(buf -> {
			buf.put(readBytes(4));
			buf.rewind();
			return buf.getInt();
		});
	}

	/**
	 * @param i
	 * @throws GameException
	 */
	public void writeInt(int i) throws GameException {
		byteBuffer(buf -> {
			buf.putInt(i);
			buf.rewind();
			writeBytes(buf.array());
			return null;
		});
	}

	/**
	 * @return the float
	 * @throws GameException
	 */
	public float readFloat() throws GameException {
		return byteBuffer(buf -> {
			buf.put(readBytes(4));
			buf.rewind();
			return buf.getFloat();
		});
	}

	/**
	 * @param f
	 * @throws GameException
	 */
	public void writeFloat(float f) throws GameException {
		byteBuffer(buf -> {
			buf.putFloat(f);
			buf.rewind();
			writeBytes(buf.array());
			return null;
		});
	}

	/**
	 * @param floats
	 * @throws GameException
	 */
	public void writeFloats(float[] floats) throws GameException {
		for (float f : floats) {
			writeFloat(f);
		}
	}

	/**
	 * @param length
	 * @return the floats
	 * @throws GameException
	 */
	public float[] readFloats(int length) throws GameException {
		float[] floats = new float[length];
		for (int i = 0; i < floats.length; i++) {
			floats[i] = readFloat();
		}
		return floats;
	}

	/**
	 * @param ints
	 * @throws GameException
	 */
	public void writeInts(int[] ints) throws GameException {
		for (int f : ints) {
			writeInt(f);
		}
	}

	/**
	 * @param length
	 * @return the ints
	 * @throws GameException
	 */
	public int[] readInts(int length) throws GameException {
		int[] ints = new int[length];
		for (int i = 0; i < ints.length; i++) {
			ints[i] = readInt();
		}
		return ints;
	}

	private <T> T byteBuffer(GameFunction<ByteBuffer, T> function) throws GameException {
		bufLock.lock();
		buf.rewind();
		T t = function.apply(buf);
		bufLock.unlock();
		return t;
	}

	/**
	 * @param length
	 * @return the bytes
	 * @throws GameException
	 */
	public byte[] readBytes(int length) throws GameException {
		byte[] bytes = new byte[length];
		length = readBytes(bytes);
		return Arrays.copyOf(bytes, length);
	}

	/**
	 * @return if this {@link ResourceStream} is a directory
	 */
	public boolean isDirectory() {
		return directory;
	}

	/**
	 * @return the path
	 */
	public Path getPath() {
		return path;
	}

}
