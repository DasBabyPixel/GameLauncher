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
import gamelauncher.engine.GameException;
import gamelauncher.engine.util.GameFunction;

public class ResourceStream implements AutoCloseable {

	private final Path path;
	private final boolean directory;
	private final InputStream in;
	private final OutputStream out;

	private final Lock bufLock = new ReentrantLock(true);
	private final ByteBuffer buf = ByteBuffer.allocate(4);

	private static final int NULL = -1;

	public ResourceStream(Path path, boolean directory, InputStream in, OutputStream out) {
		this.path = path;
		this.directory = directory;
		this.in = in;
		this.out = out;
	}

	public boolean hasInputStream() {
		return in != null;
	}

	public InputStream getInputStream() {
		return in;
	}

	public boolean hasOutputStream() {
		return out != null;
	}

	public OutputStream getOutputStream() {
		return out;
	}

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
		if (in != null) {
			in.close();
		}
		if (out != null) {
			out.close();
		}
	}

	public void cleanup() throws GameException {
		try {
			close();
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

	public PNGDecoder newPNGDecoder() throws GameException {
		try {
			return new PNGDecoder(in);
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

	public String readUTF8(int length) throws GameException {
		return new String(readBytes(length), StandardCharsets.UTF_8);
	}

	public void writeUTF8(String string) throws GameException {
		writeBytes(string.getBytes(StandardCharsets.UTF_8));
	}

	public String readUTF8Fully() throws GameException {
		return new String(readAllBytes(), StandardCharsets.UTF_8);
	}

	public void swriteUTF8(String string) throws GameException {
		if (string == null) {
			swriteBytes(null);
		} else {
			swriteBytes(string.getBytes(StandardCharsets.UTF_8));
		}
	}

	public String sreadUTF8() throws GameException {
		byte[] b = sreadBytes();
		if (b == null)
			return null;
		return new String(b, StandardCharsets.UTF_8);
	}

	public void swriteFloats(float[] floats) throws GameException {
		if (floats == null) {
			writeInt(NULL);
		} else {
			writeInt(floats.length);
			writeFloats(floats);
		}
	}

	public float[] sreadFloats() throws GameException {
		int len = readInt();
		if (len == NULL)
			return null;
		return readFloats(len);
	}

	public void swriteInts(int[] ints) throws GameException {
		if (ints == null) {
			writeInt(NULL);
		} else {
			writeInt(ints.length);
			writeInts(ints);
		}
	}

	public int[] sreadInts() throws GameException {
		int len = readInt();
		if (len == NULL)
			return null;
		return readInts(len);
	}

	public void swriteBytes(byte[] bytes) throws GameException {
		if (bytes == null) {
			writeInt(NULL);
		} else {
			writeInt(bytes.length);
			writeBytes(bytes);
		}
	}

	public byte[] sreadBytes() throws GameException {
		int len = readInt();
		if (len == NULL)
			return null;
		return readBytes(len);
	}

	public String readUTF8FullyClose() throws GameException {
		String utf8 = readUTF8Fully();
		try {
			close();
		} catch (IOException ex) {
			throw new GameException(ex);
		}
		return utf8;
	}

	public int readBytes(byte[] bytes) throws GameException {
		try {
			return in.read(bytes);
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

	public byte[] readAllBytes() throws GameException {
		byte[] buffer = new byte[16384];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read;
		while ((read = readBytes(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
		return out.toByteArray();
	}

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

	public void writeByte(byte b) throws GameException {
		try {
			out.write(b);
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

	public void writeBytes(byte[] b) throws GameException {
		try {
			out.write(b);
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

	public int readInt() throws GameException {
		return byteBuffer(buf -> {
			buf.put(readBytes(4));
			buf.rewind();
			return buf.getInt();
		});
	}

	public void writeInt(int i) throws GameException {
		byteBuffer(buf -> {
			buf.putInt(i);
			buf.rewind();
			writeBytes(buf.array());
			return null;
		});
	}

	public float readFloat() throws GameException {
		return byteBuffer(buf -> {
			buf.put(readBytes(4));
			buf.rewind();
			return buf.getFloat();
		});
	}

	public void writeFloat(float f) throws GameException {
		byteBuffer(buf -> {
			buf.putFloat(f);
			buf.rewind();
			writeBytes(buf.array());
			return null;
		});
	}

	public void writeFloats(float[] floats) throws GameException {
		for (float f : floats) {
			writeFloat(f);
		}
	}

	public float[] readFloats(int length) throws GameException {
		float[] floats = new float[length];
		for (int i = 0; i < floats.length; i++) {
			floats[i] = readFloat();
		}
		return floats;
	}

	public void writeInts(int[] ints) throws GameException {
		for (int f : ints) {
			writeInt(f);
		}
	}

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

	public byte[] readBytes(int length) throws GameException {
		byte[] bytes = new byte[length];
		length = readBytes(bytes);
		return Arrays.copyOf(bytes, length);
	}

	public boolean isDirectory() {
		return directory;
	}

	public Path getPath() {
		return path;
	}
}
