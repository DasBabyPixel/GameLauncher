package gamelauncher.engine.util;

import org.joml.Math;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author DasBabyPixel
 */
public class ByteBufferBackedInputStream extends InputStream {

    private final ByteBuffer buffer;

    /**
     * @param buffer
     */
    public ByteBufferBackedInputStream(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override public int read() throws IOException {
        if (!buffer.hasRemaining()) {
            return -1;
        }
        return buffer.get() & 0xFF;
    }

    @Override public int read(byte[] b, int off, int len) throws IOException {
        if (!buffer.hasRemaining()) {
            return -1;
        }
        len = Math.min(len, buffer.remaining());
        buffer.get(b, off, len);
        return len;
    }

    /**
     * @return the buffer of this stream
     */
    public ByteBuffer getBuffer() {
        return buffer;
    }
}
