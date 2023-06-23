/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl.util;

import gamelauncher.engine.util.Debug;
import gamelauncher.gles.util.MemoryManagement;
import java8.util.function.Function;
import org.lwjgl.system.MemoryStack;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

import static org.lwjgl.system.APIUtil.DEBUG_STREAM;

public class LWJGLMemoryStack implements gamelauncher.gles.util.MemoryStack {
    protected final MemoryStack stack = MemoryStack.stackGet();

    private LWJGLMemoryStack() {
    }

    public static LWJGLMemoryStack newStack() {
        if (Debug.debug) {
            return new DebugMemoryStack();
        }
        return new LWJGLMemoryStack();
    }

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

    public static class DebugMemoryStack extends LWJGLMemoryStack {

        private Object[] debugFrames;

        protected DebugMemoryStack() {
            debugFrames = new Object[8];
        }

        private static void reportAsymmetricPop(Object pushed, Object popped) {
            Thread.dumpStack();
            DEBUG_STREAM.format("[GameLauncher] Asymmetric pop detected:\n\tPUSHED: %s\n\tPOPPED: %s\n\tTHREAD: %s\n", pushed, popped, Thread.currentThread());
        }

        static Object stackWalkGetMethod(Class<?>... afterAny) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

            for (int i = 3; i < stackTrace.length; i++) {
                boolean hasAny = false;
                for (Class<?> after : afterAny) {
                    if (stackTrace[i].getClassName().startsWith(after.getName())) {
                        hasAny = true;
                        break;
                    }
                }
                if (!hasAny) return stackTrace[i];
            }

            return null;
        }

        @Nullable static Object stackWalkCheckPop(Object pushedObj, Class<?>... afterAny) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

            f1:
            for (int i = 3; i < stackTrace.length; i++) {
                StackTraceElement element = stackTrace[i];
                for (Class<?> after : afterAny) {
                    if (element.getClassName().startsWith(after.getName())) {
                        continue f1;
                    }
                }

                StackTraceElement pushed = (StackTraceElement) pushedObj;
                if (isSameMethod(element, pushed)) {
                    return null;
                }

                if (isAutoCloseable(element, pushed) && i + 1 < stackTrace.length) {
                    // Some runtimes use a separate method to call AutoCloseable::close in try-with-resources blocks.
                    // That method suppresses any exceptions thrown by close if necessary.
                    // When that happens, the pop is 1 level deeper than expected.
                    element = stackTrace[i + 1];
                    if (isSameMethod(pushed, stackTrace[i + 1])) {
                        return null;
                    }
                }

                return element;
            }

            return null;
        }

        private static boolean isSameMethod(StackTraceElement a, StackTraceElement b) {
            return isSameMethod(a, b, b.getMethodName());
        }

        private static boolean isSameMethod(StackTraceElement a, StackTraceElement b, String methodName) {
            return a.getMethodName().equals(methodName) && a.getClassName().equals(b.getClassName()) && Objects.equals(a.getFileName(), b.getFileName());
        }

        private static boolean isAutoCloseable(StackTraceElement element, StackTraceElement pushed) {
            // Java 9 try-with-resources: synthetic $closeResource
            if (isSameMethod(element, pushed, "$closeResource")) {
                return true;
            }

            // Kotlin T.use: kotlin.AutoCloseable::closeFinally
            return "closeFinally".equals(element.getMethodName()) && "AutoCloseable.kt".equals(element.getFileName());
        }

        @Override public gamelauncher.gles.util.MemoryStack push() {
            if (stack.getFrameIndex() == debugFrames.length) {
                frameOverflow();
            }

            debugFrames[stack.getFrameIndex()] = stackWalkGetMethod(LWJGLMemoryManagement.class, MemoryManagement.class, LWJGLMemoryStack.class);

            return super.push();
        }

        @Override public gamelauncher.gles.util.MemoryStack pop() {
            Object pushed = debugFrames[stack.getFrameIndex() - 1];
            Object popped = stackWalkCheckPop(pushed, LWJGLMemoryManagement.class, MemoryManagement.class, LWJGLMemoryStack.class);
            if (popped != null) {
                reportAsymmetricPop(pushed, popped);
            }
            debugFrames[stack.getFrameIndex() - 1] = null;

            return super.pop();
        }

        private void frameOverflow() {
            debugFrames = Arrays.copyOf(debugFrames, debugFrames.length * 3 / 2);
        }
    }
}
