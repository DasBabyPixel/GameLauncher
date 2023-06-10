/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.render;

import de.dasbabypixel.api.property.NumberValue;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * @author DasBabyPixel
 */
public class FrameCounter {

    private static final long second = TimeUnit.SECONDS.toNanos(1);
    private final NumberValue limit = NumberValue.withValue(0D);
    private final NumberValue frameNanos = NumberValue.withValue(1L);
    private final Buffer buffer = new Buffer();
    private final AtomicInteger lastFps = new AtomicInteger(0);
    private final AtomicInteger lastFrameCount = new AtomicInteger(0);
    private final Collection<Consumer<Integer>> updateListeners = ConcurrentHashMap.newKeySet();
    private final Collection<Consumer<Float>> avgUpdateListeners = ConcurrentHashMap.newKeySet();

    /**
     * @return the update listeners
     */
    public Collection<Consumer<Integer>> updateListeners() {
        return this.updateListeners;
    }

    public void addUpdateListener(Consumer<Integer> fpsConsumer) {
        this.updateListeners.add(fpsConsumer);
    }

    public void addAvgUpdateListener(Consumer<Float> avgFpsConsumer) {
        this.avgUpdateListeners.add(avgFpsConsumer);
    }

    /**
     * @return the average update listeners
     */
    public Collection<Consumer<Float>> avgUpdateListeners() {
        return this.avgUpdateListeners;
    }

    private void offer(long nanos) {
        this.buffer.addFrame(nanos);
        int fps = fps();
        if (this.lastFps.getAndSet(fps) != fps) {
            this.updateListeners.forEach(l -> l.accept(fps));
        }
        int average;
        synchronized (buffer.frames5Second) {
            average = buffer.frames5Second.size();
        }
        if (this.lastFrameCount.getAndSet(average) != average) {
            this.avgUpdateListeners.forEach(l -> l.accept(average / 5.0F));
        }
    }

    /**
     *
     */
    public void frameNoWait() {
        this.offer(System.nanoTime());
    }

    /**
     * @param nanoSleeper the nanosleeper function
     */
    public void frame(Consumer<Long> nanoSleeper) {
        float limit = this.limit();
        if (limit == 0) {
            this.offer(System.nanoTime());
        } else {
            boolean empty;
            synchronized (buffer.frames5Second) {
                empty = buffer.frames5Second.isEmpty();
            }
            if (!empty) {
                long frameNanos = this.frameNanos.longValue();
                long nextFrame = this.buffer.lastFrame.get() + frameNanos;
                if (nextFrame - System.nanoTime() > 0) {
                    nanoSleeper.accept(nextFrame - System.nanoTime());
                }
            }
            this.offer(System.nanoTime());
        }
    }

    /**
     * @return the current fps
     */
    public int fps() {
        synchronized (buffer.frames1Second) {
            return this.buffer.frames1Second.size();
        }
    }

    /**
     * @return the average fps over the last five seconds
     */
    public float fpsAvg() {
        synchronized (buffer.frames5Second) {
            return this.buffer.frames5Second.size() / 5F;
        }
    }

    /**
     * @return the frameLimit
     */
    public float limit() {
        return this.limit.floatValue();
    }

    /**
     * Sets the frameLimit
     *
     * @param limit the new limit
     */
    public void limit(float limit) {
        if (limit <= 0) {
            this.limit.number(0);
            this.frameNanos.number(0);
        } else {
            this.limit.number(limit);
            this.frameNanos.number(FrameCounter.second / limit);
        }
    }

    private static class Buffer {

        private final LongList frames1Second = new LongArrayList();
        private final LongList frames5Second = new LongArrayList();

        private final AtomicLong lastFrame = new AtomicLong();

        public void addFrame(long frame) {
            this.removeOldFrames();
            synchronized (frames1Second) {
                this.frames1Second.add(frame);
            }
            synchronized (frames5Second) {
                this.frames5Second.add(frame);
            }
            this.lastFrame.set(frame);
        }

        private void removeOldFrames() {
            long compareTo = System.nanoTime() - FrameCounter.second * 5;
            synchronized (this.frames5Second) {
                while (!this.frames5Second.isEmpty()) {
                    long first = this.frames5Second.getLong(0);
                    if (compareTo - first > 0) {
                        this.frames5Second.removeLong(0);
                        continue;
                    }
                    break;
                }
            }
            compareTo = System.nanoTime() - FrameCounter.second;
            synchronized (frames1Second) {
                while (!this.frames1Second.isEmpty()) {
                    long first = this.frames1Second.getLong(0);
                    if (compareTo - first > 0) {
                        this.frames1Second.removeLong(0);
                        continue;
                    }
                    break;
                }
            }
        }
    }
}
