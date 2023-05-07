package gamelauncher.gles.util;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.data.DataUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * All Buffers allocated by this class must have the native byte order
 */
@Api
public interface MemoryManagement {

    @Api ByteBuffer alloc(int size);

    @Api ByteBuffer calloc(int size);

    @Api ByteBuffer allocDirect(int size);

    @Api ByteBuffer callocDirect(int size);

    @Api default IntBuffer allocInt(int size) {
        return alloc(DataUtil.BYTES_INT * size).asIntBuffer();
    }

    @Api default IntBuffer callocInt(int size) {
        return calloc(DataUtil.BYTES_INT * size).asIntBuffer();
    }

    @Api default FloatBuffer allocFloat(int size) {
        return alloc(DataUtil.BYTES_FLOAT * size).asFloatBuffer();
    }

    @Api default FloatBuffer callocFloat(int size) {
        return calloc(DataUtil.BYTES_FLOAT * size).asFloatBuffer();
    }

    @Api default IntBuffer allocDirectInt(int size) {
        return allocDirect(DataUtil.BYTES_INT * size).asIntBuffer();
    }

    @Api default IntBuffer callocDirectInt(int size) {
        return callocDirect(DataUtil.BYTES_INT * size).asIntBuffer();
    }

    @Api default FloatBuffer allocDirectFloat(int size) {
        return allocDirect(DataUtil.BYTES_FLOAT * size).asFloatBuffer();
    }

    @Api default FloatBuffer callocDirectFloat(int size) {
        return callocDirect(DataUtil.BYTES_FLOAT * size).asFloatBuffer();
    }

    @Api void free(Buffer buffer);

}
