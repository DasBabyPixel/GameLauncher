/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.internal.gl;

import gamelauncher.gles.util.MemoryStack;

import java.nio.ByteBuffer;

public class AndroidMemoryStack implements MemoryStack {
    private final AndroidMemoryManagement memoryManagement;

    public AndroidMemoryStack(AndroidMemoryManagement memoryManagement) {
        this.memoryManagement = memoryManagement;
    }

    @Override public ByteBuffer alloc(int size) {
        return memoryManagement.alloc(size);
    }

    @Override public ByteBuffer calloc(int size) {
        return memoryManagement.calloc(size);
    }

    @Override public ByteBuffer allocDirect(int size) {
        return memoryManagement.allocDirect(size);
    }

    @Override public ByteBuffer callocDirect(int size) {
        return memoryManagement.callocDirect(size);
    }

    @Override public void close() {
    }

    @Override public MemoryStack push() {
        return this;
    }

    @Override public MemoryStack pop() {
        return this;
    }
}
