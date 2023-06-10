/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl.util;

import java8.util.function.Function;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

public class LWJGLMemoryStack implements gamelauncher.gles.util.MemoryStack {
    private final MemoryStack stack = MemoryStack.stackGet();

    @Override public ByteBuffer alloc(int size) {
        return stack.malloc(size);
    }

    @Override public ByteBuffer calloc(int size) {
        return stack.calloc(size);
    }

    @Override public ByteBuffer allocDirect(int size) {
        return stack.malloc(size);
    }

    @Override public ByteBuffer callocDirect(int size) {
        return stack.calloc(size);
    }

    @Override public void close() {
        pop();
    }

    @Override public gamelauncher.gles.util.MemoryStack push() {
        stack.push();
        return this;
    }

    @Override public gamelauncher.gles.util.MemoryStack pop() {
        stack.pop();
        return this;
    }

    public <T> T call(Function<MemoryStack, T> function) {
        return function.apply(stack);
    }
}
