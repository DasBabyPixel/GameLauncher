package gamelauncher.lwjgl.util;

import gamelauncher.gles.util.MemoryManagement;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class LWJGLMemoryManagement implements MemoryManagement {
    @Override
    public ByteBuffer alloc(int size) {
        return MemoryUtil.memAlloc(size);
    }

    @Override
    public ByteBuffer calloc(int size) {
        return MemoryUtil.memCalloc(size);
    }

    @Override
    public ByteBuffer allocDirect(int size) {
        return MemoryUtil.memAlloc(size);
    }

    @Override
    public ByteBuffer callocDirect(int size) {
        return MemoryUtil.memCalloc(size);
    }

    @Override
    public void free(Buffer buffer) {
        MemoryUtil.memFree(buffer);
    }
}
