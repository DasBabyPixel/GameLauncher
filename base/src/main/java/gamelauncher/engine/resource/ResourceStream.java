package gamelauncher.engine.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.matthiasmann.twl.utils.PNGDecoder;
import gamelauncher.engine.GameException;
import gamelauncher.engine.util.GameFunction;

public class ResourceStream implements AutoCloseable {

	private final ResourcePath path;
	private final boolean directory;
	private final InputStream in;
	private final OutputStream out;

	private final Lock bufLock = new ReentrantLock(true);
	private final ByteBuffer buf = ByteBuffer.allocate(4);

	public ResourceStream(ResourcePath path, boolean directory, InputStream in, OutputStream out) {
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
//		bufLock.lock();
//		buf.rewind();
//		buf.put(readBytes(4));
//		buf.rewind();
//		int i = buf.getInt();
//		bufLock.unlock();
//		return i;
	}

	public void writeInt(int i) throws GameException {
		byteBuffer(buf -> {
			buf.putInt(i);
			buf.rewind();
			writeBytes(buf.array());
			return null;
		});
//		bufLock.lock();
//		buf.rewind();
//		buf.putInt(i);
//		buf.rewind();
//		writeBytes(buf.array());
//		bufLock.unlock();
	}

	public float readFloat() throws GameException {
		return byteBuffer(buf -> {
			buf.put(readBytes(4));
			buf.rewind();
			return buf.getFloat();
		});
//		bufLock.lock();
//		buf.rewind();
//		buf.put(readBytes(4));
//		buf.rewind();
//		float f = buf.getFloat();
//		bufLock.unlock();
//		return f;
	}

	public void writeFloat(float f) throws GameException {
		byteBuffer(buf -> {
			buf.putFloat(f);
			buf.rewind();
			writeBytes(buf.array());
			return null;
		});
//		bufLock.lock();
//		buf.rewind();
//		buf.putFloat(f);
//		buf.rewind();
//		writeBytes(buf.array());
//		bufLock.unlock();
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

	public ResourcePath getPath() {
		return path;
	}
}
