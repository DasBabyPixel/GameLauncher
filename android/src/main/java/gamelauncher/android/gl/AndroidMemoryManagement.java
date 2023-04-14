/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.gl;

import gamelauncher.gles.util.MemoryManagement;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AndroidMemoryManagement implements MemoryManagement {
    @Override
    public ByteBuffer alloc(int size) {
        return ByteBuffer.allocate(size);
    }

    @Override
    public ByteBuffer calloc(int size) {
        return ByteBuffer.allocate(size);
    }

    @Override
    public ByteBuffer allocDirect(int size) {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
    }

    @Override
    public ByteBuffer callocDirect(int size) {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
    }

    @Override
    public void free(Buffer buffer) {
        // GC handles this
    }
}
