/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl.util;

import gamelauncher.gles.util.MemoryManagement;
import it.unimi.dsi.fastutil.longs.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

public class LWJGLMemoryManagement implements MemoryManagement {
    private static final ThreadLocal<LWJGLMemoryStack> TLS = ThreadLocal.withInitial(LWJGLMemoryStack::new);
    public final LongList allocBuffers = new LongArrayList();
    public final Long2ObjectMap<Thread> threads = new Long2ObjectOpenHashMap<>();
    public final AtomicInteger count = new AtomicInteger();
    public final Long2IntMap capacity = new Long2IntOpenHashMap();

    @Override public synchronized ByteBuffer alloc(int size) {
        ByteBuffer buf = MemoryUtil.memAlloc(size);
        add(buf);
        return buf;
    }

    @Override public synchronized ByteBuffer calloc(int size) {
        ByteBuffer buf = MemoryUtil.memCalloc(size);
        add(buf);
        return buf;
    }

    @Override public synchronized ByteBuffer allocDirect(int size) {
        ByteBuffer buf = MemoryUtil.memAlloc(size);
        add(buf);
        return buf;
    }

    @Override public synchronized ByteBuffer callocDirect(int size) {
        ByteBuffer buf = MemoryUtil.memCalloc(size);
        add(buf);
        return buf;
    }

    @Override public LWJGLMemoryStack stackGet() {
        return TLS.get();
    }

    @Override public LWJGLMemoryStack stackPush() {
        return (LWJGLMemoryStack) MemoryManagement.super.stackPush();
    }

    @Override public LWJGLMemoryStack stackPop() {
        return (LWJGLMemoryStack) MemoryManagement.super.stackPop();
    }

    @Override public synchronized void free(Buffer buffer) {
        buffer.position(0);
        synchronized (allocBuffers) {
            if (!allocBuffers.contains(hash(buffer))) {
                System.out.println("DOESNT CONTAIN " + hash(buffer));
                Thread.dumpStack();
            } else {
                Thread th = threads.remove(hash(buffer));
                if (th != Thread.currentThread()) {
                    System.out.println("Dealloc: Old Thread: " + th.getName());
                    Thread.dumpStack();
                }

                allocBuffers.rem(hash(buffer));

                ByteBuffer bb = MemoryUtil.memByteBuffer(MemoryUtil.memAddress(buffer), capacity.get(hash(buffer)));
                for (int i = 0; i < bb.capacity(); i++) {
                    bb.put(i, (byte) 0xA3);
                }
            }
        }
        count.decrementAndGet();
        MemoryUtil.memFree(buffer);
    }

    private void add(ByteBuffer buffer) {
        count.incrementAndGet();
        synchronized (allocBuffers) {
            capacity.put(hash(buffer), buffer.capacity());
            allocBuffers.add(hash(buffer));
            threads.put(hash(buffer), Thread.currentThread());
        }
    }

    private long hash(Buffer o) {
        return MemoryUtil.memAddress(o);
    }
}
