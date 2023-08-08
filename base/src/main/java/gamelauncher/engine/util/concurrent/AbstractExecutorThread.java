/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.concurrent;

import com.lmax.disruptor.EventPoller;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.Debug;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.engine.util.logging.Logger;
import java8.util.concurrent.CompletableFuture;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author DasBabyPixel
 */
public abstract class AbstractExecutorThread extends AbstractGameThread implements ExecutorThread {

    private static final Logger logger = Logger.logger(AbstractExecutorThread.class);
    private final RingBuffer<QueueEntry> ringBuffer = RingBuffer.createMultiProducer(QueueEntry::new, 1024, new SleepingWaitStrategy());
    private final EventPoller<QueueEntry> poller = ringBuffer.newPoller();
    private final CompletableFuture<Void> exitFuture = new CompletableFuture<>();
    private final AtomicBoolean work = new AtomicBoolean();
    /**
     * the current entry
     */
    public WrapperEntry currentEntry;
    protected volatile boolean exit = false;
    private boolean skipNextSignalWait = false;

    {
        ringBuffer.addGatingSequences(poller.getSequence());
    }

    public AbstractExecutorThread(GameLauncher launcher, ThreadGroup group) {
        super(launcher, group, (Runnable) null);
    }

    /**
     * @return the exit future
     */
    public final CompletableFuture<Void> exitFuture() {
        return this.exitFuture;
    }

    /**
     * @return the exit future
     */
    public final CompletableFuture<Void> exit() {
        this.exit = true;
        this.signal();
        return this.exitFuture();
    }

    @Override protected final CompletableFuture<Void> cleanup0() throws GameException {
        return this.exit();
    }

    @Override public final String name() {
        return getName();
    }

    protected abstract void startExecuting() throws GameException;

    protected abstract void stopExecuting() throws GameException;

    protected abstract void workExecution() throws GameException;

    protected void loop() throws GameException {
        if (this.shouldWaitForSignal()) {
            this.waitForSignal();
        }
        this.workQueue();
        this.workExecution();
    }

    protected void waitForSignal() {
        if (this.exit) return;
        if (this.skipNextSignalWait) {
            this.skipNextSignalWait = false;
            return;
        }
        while (!this.work.compareAndSet(true, false)) {
            Threads.park();
        }
    }

    protected void waitForSignalTimeout(long nanos) {
        if (this.exit) return;
        if (this.skipNextSignalWait) {
            this.skipNextSignalWait = false;
            return;
        }
        final long begin = System.nanoTime();
        this.skipNextSignalWait = true;
        while (!this.work.compareAndSet(true, false)) {
            long parktime = begin + nanos - System.nanoTime();
            if (parktime < 0) {
                this.skipNextSignalWait = false;
                break;
            }
            Threads.park(parktime);
        }
    }

    @Override public final void run() {
        try {
            AbstractExecutorThread.logger.debug("Starting " + this.getName());
            this.startExecuting();
            while (!this.exit) {
                this.loop();
            }
            this.loop();
            this.stopExecuting();
            stopTracking();
            this.exitFuture.complete(null);
        } catch (Throwable ex) {
            stopTracking();
            GameException st = this.buildStacktrace();
            st.initCause(ex);
            launcher().handleError(st);
        }
        AbstractExecutorThread.logger.debug("Stopping " + this.getName());
    }

    private void work(GameRunnable run, CompletableFuture<Void> fut) {
        try {
            run.run();
            fut.complete(null);
        } catch (Throwable ex) {
            GameException ex2 = this.buildStacktrace();
            ex2.initCause(ex);
            AbstractExecutorThread.logger.error(ex2);
            fut.completeExceptionally(ex2);
        }
    }

    /**
     * @return the stacktrace of the current {@link #currentEntry}
     */
    public GameException buildStacktrace() {
        GameException ex = new GameException("Exception in ExecutorThread");
        ex.setStackTrace(new StackTraceElement[0]);
        if (this.currentEntry != null) {
            Throwable t = this.currentEntry.calculateCause();
            if (t != null) {
                ex.addSuppressed(t);
            }
        }
        return ex;
    }

    protected boolean shouldWaitForSignal() {
        return true;
    }

    protected void signal() {
        work.set(true);
        Threads.unpark(this);
    }

    @Override public CompletableFuture<Void> submit(GameRunnable runnable) {
        if (runnable == null) throw new IllegalArgumentException("Null runnable");
        CompletableFuture<Void> fut = new CompletableFuture<>();
        if (Threads.currentThread() == this) {
            this.work(runnable, fut);
        } else {
            ringBuffer.publishEvent((event, sequence, arg0, arg1, arg2) -> event.set(arg1, arg0, arg2), fut, WrapperEntry.newEntry(), runnable);
            this.signal();
        }
        return fut;
    }

    @Override public final void workQueue() {
        try {
            poller.poll((e, sequence, endOfBatch) -> {
                if (Debug.calculateThreadStacks) this.currentEntry = e.entry;
                this.work(e.run, e.fut);
                if (Debug.calculateThreadStacks) this.currentEntry = null;
                e.clear();
                return true;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static final class QueueEntry {

        public WrapperEntry entry;
        public CompletableFuture<Void> fut;
        public GameRunnable run;

        public void set(WrapperEntry entry, CompletableFuture<Void> fut, GameRunnable run) {
            this.entry = entry;
            this.fut = fut;
            this.run = run;
        }

        public void clear() {
            entry = null;
            fut = null;
            run = null;
        }
    }
}
