/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.concurrent;

import gamelauncher.engine.util.GameException;

import java.util.Arrays;

/**
 * @author DasBabyPixel
 */
public class WrapperEntry {

    /**
     * the stacktrace for this entry
     */
    public final StackTraceElement[] stacktrace;

    /**
     * the cause of this entry
     */
    public final WrapperEntry cause;

    /**
     * the thread for this entry
     */
    public final Thread thread;

    public WrapperEntry(StackTraceElement[] stacktrace, WrapperEntry cause, Thread thread) {
        this.stacktrace = stacktrace;
        this.cause = cause;
        this.thread = thread;
    }

    /**
     * @return a new {@link WrapperEntry} for this thread
     */
    public static WrapperEntry newEntry() {
        if (Threads.calculateThreadStacks) {
            StackTraceElement[] stack = new Exception().getStackTrace();
            stack = Arrays.copyOfRange(stack, 2, stack.length);
            return new WrapperEntry(stack, WrapperEntry.cause(), Thread.currentThread());
        }
        return new WrapperEntry(null, WrapperEntry.cause(), Thread.currentThread());
    }

    private static WrapperEntry cause() {
        if (Thread.currentThread() instanceof AbstractExecutorThread) {
            AbstractExecutorThread aet = (AbstractExecutorThread) Thread.currentThread();
            return aet.currentEntry;
        }
        WrapperExecutorThreadService.WrapperCallable<?> callable = WrapperExecutorThreadService.WrapperCallable.threadLocal.get();
        if (callable != null) {
            return callable.entry;
        }
        return null;
    }

    /**
     * @return a new Throwable with the given cause and stacktrace for utility
     */
    public Throwable calculateCause() {
        GameException c = new GameException("Thread: " + this.thread.getName());
        c.setStackTrace(this.stacktrace);
        if (this.cause != null) {
            Throwable cause = this.cause.calculateCause();
            if (cause != null) c.initCause(cause);
        }
        return c;
    }

    @Override public String toString() {
        return "WrapperEntry [cause=" + this.cause + ", thread=" + this.thread.getName() + "]";
    }

}
