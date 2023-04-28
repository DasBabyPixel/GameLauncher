/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.concurrent;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.collections.Collections;
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.engine.util.logging.Logger;
import java8.util.concurrent.CompletableFuture;

import java.util.Deque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * @author DasBabyPixel
 */
public abstract class AbstractExecutorThread extends AbstractGameThread implements ExecutorThread {

    private static final Logger logger = Logger.logger(AbstractExecutorThread.class);
    private final Deque<QueueEntry> queue = Collections.newConcurrentDeque();
    private final CompletableFuture<Void> exitFuture = new CompletableFuture<>();
    private final AtomicBoolean work = new AtomicBoolean();
    /**
     * the current entry
     */
    public WrapperEntry currentEntry;
    protected volatile boolean exit = false;
    private boolean skipNextSignalWait = false;

    public AbstractExecutorThread(GameLauncher launcher, ThreadGroup group) {
        super(launcher, group, (Runnable) null);
    }

    /**
     * @return the exit future
     */
    public CompletableFuture<Void> exitFuture() {
        return this.exitFuture;
    }

    /**
     * @return the exit future
     */
    public CompletableFuture<Void> exit() {
        this.exit = true;
        this.signal();
        return this.exitFuture();
    }

    @Override protected void cleanup0() throws GameException {
        Threads.waitFor(this.exit());
    }

    @Override public String name() {
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
            this.exitFuture.complete(null);
            if (!this.cleanedUp) {
                this.cleanedUp = true;
                AbstractGameResource.logCleanup(this);
            }
        } catch (Throwable ex) {
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
        } catch (GameException ex) {
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

    @Api protected boolean shouldHandle(QueueEntry entry) {
        return true;
    }

    protected boolean shouldWaitForSignal() {
        return true;
    }

    protected void signal() {
        work.set(true);
        Threads.unpark((ParkableThread) this);
    }

    @Override public final CompletableFuture<Void> submitLast(GameRunnable runnable) {
        CompletableFuture<Void> fut = new CompletableFuture<>();
        if (Thread.currentThread() == this) {
            this.work(runnable, fut);
        } else {
            this.queue.offerLast(new QueueEntry(fut, runnable, WrapperEntry.newEntry()));
            this.signal();
        }
        return fut;
    }

    @Override public final CompletableFuture<Void> submitFirst(GameRunnable runnable) {
        CompletableFuture<Void> fut = new CompletableFuture<>();
        if (Thread.currentThread() == this) {
            this.work(runnable, fut);
        } else {
            this.queue.offerFirst(new QueueEntry(fut, runnable, WrapperEntry.newEntry()));
            this.signal();
        }
        return fut;
    }

    @Override public void park() {
        if (Thread.currentThread() != this) {
            throw new SecurityException("May not call this from any other thread than self");
        }
        LockSupport.park();
        workQueue();
    }

    @Override public void park(long nanos) {
        if (Thread.currentThread() != this) {
            throw new SecurityException("May not call this from any other thread than self");
        }
        LockSupport.parkNanos(nanos);
        workQueue();
    }

    @Override public void unpark() {
        LockSupport.unpark(this);
    }

    @Override public final void workQueue() {
        QueueEntry e;
        while ((e = this.queue.pollFirst()) != null) {
            if (!this.shouldHandle(e)) {
                this.queue.offerFirst(e);
                return;
            }
            if (Threads.calculateThreadStacks) {
                this.currentEntry = e.entry;
            }
            this.work(e.run, e.fut);
            if (Threads.calculateThreadStacks) {
                this.currentEntry = null;
            }
        }
    }

    protected static final class QueueEntry {

        public final WrapperEntry entry;
        public final CompletableFuture<Void> fut;
        public final GameRunnable run;

        public QueueEntry(CompletableFuture<Void> fut, GameRunnable run, WrapperEntry entry) {
            this.fut = fut;
            this.run = run;
            this.entry = entry;
        }
    }
}
