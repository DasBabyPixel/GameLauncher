package gamelauncher.gles.util;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * All Buffers allocated by this class must have the native byte order
 */
public interface MemoryManagement {

    ByteBuffer alloc(int size);

    ByteBuffer calloc(int size);

    ByteBuffer allocDirect(int size);

    ByteBuffer callocDirect(int size);

    default IntBuffer allocInt(int size) {
        return alloc(Integer.BYTES * size).asIntBuffer();
    }

    default IntBuffer callocInt(int size) {
        return calloc(Integer.BYTES * size).asIntBuffer();
    }

    default FloatBuffer allocFloat(int size) {
        return alloc(Float.BYTES * size).asFloatBuffer();
    }

    default FloatBuffer callocFloat(int size) {
        return calloc(Float.BYTES * size).asFloatBuffer();
    }

    default IntBuffer allocDirectInt(int size) {
        return allocDirect(Integer.BYTES * size).asIntBuffer();
    }

    default IntBuffer callocDirectInt(int size) {
        return callocDirect(Integer.BYTES * size).asIntBuffer();
    }

    default FloatBuffer allocDirectFloat(int size) {
        return allocDirect(Float.BYTES * size).asFloatBuffer();
    }

    default FloatBuffer callocDirectFloat(int size) {
        return callocDirect(Float.BYTES * size).asFloatBuffer();
    }

    void free(Buffer buffer);

}
