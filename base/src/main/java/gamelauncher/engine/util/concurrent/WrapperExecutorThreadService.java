/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.concurrent;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameCallable;
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.engine.util.logging.Logger;
import java8.util.concurrent.CompletableFuture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author DasBabyPixel
 */
public class WrapperExecutorThreadService implements ExecutorThreadService {

    static final AtomicInteger id = new AtomicInteger();
    private static final Logger logger = Logger.logger(WrapperExecutorThreadService.class);
    private final CompletableFuture<Void> exitFuture = new CompletableFuture<>();
    private final ExecutorService service;

    public WrapperExecutorThreadService(ExecutorService service) {
        this.service = service;
        Waiter waiter = new Waiter();
        waiter.start();
    }

    @Override public String name() {
        return service.toString();
    }

    @Override public CompletableFuture<Void> submit(GameRunnable runnable) {
        return submit(runnable.toCallable());
    }

    @Override public <T> CompletableFuture<T> submit(GameCallable<T> callable) {
        WrapperCallable<T> w = new WrapperCallable<>(callable, WrapperEntry.newEntry());
        service.submit(w);
        return w.fut;
    }

    @Override public Executor executor() {
        return service;
    }

    @Override public CompletableFuture<Void> exit() {
        service.shutdown();
        return exitFuture;
    }

    @Override public CompletableFuture<Void> exitFuture() {
        return exitFuture;
    }

    @Override public Collection<GameRunnable> exitNow() {
        Collection<Runnable> col = service.shutdownNow();
        Collection<GameRunnable> c2 = new ArrayList<>();
        for (Runnable runnable : col) {
            c2.add(((WrapperCallable<?>) runnable).callable.toRunnable());
        }
        return c2;
    }

    @Override public void workQueue() {
        throw new UnsupportedOperationException();
    }

    static class WrapperCallable<T> implements Runnable {

        static final ThreadLocal<WrapperCallable<?>> threadLocal = new ThreadLocal<>();

        final GameCallable<T> callable;

        final WrapperEntry entry;

        final CompletableFuture<T> fut = new CompletableFuture<>();

        public WrapperCallable(GameCallable<T> callable, WrapperEntry entry) {
            this.callable = callable;
            this.entry = entry;
        }

        @Override public void run() {
            try {
                if (threadLocal.get() != null) {
                    throw new IllegalStateException("Shouldn't happen");
                }
                threadLocal.set(this);
                fut.complete(callable.call());
            } catch (Throwable ex) {
                GameException ex2 = buildStacktrace();
                ex2.initCause(ex);
                logger.error(ex2);
                fut.completeExceptionally(ex2);
            } finally {
                threadLocal.remove();
            }
        }

        public GameException buildStacktrace() {
            GameException ex = new GameException("Exception in ExecutorService");
            if (entry != null) {
                Throwable t = entry.calculateCause();
                if (t != null) ex.addSuppressed(t);
            }
            return ex;
        }

    }

    class Waiter extends Thread {

        public Waiter() {
            super("Waiter-" + id.incrementAndGet());
            setDaemon(true);
        }

        @Override public void run() {
            while (!service.isShutdown()) {
                try {
                    //noinspection ResultOfMethodCallIgnored
                    service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            exitFuture.complete(null);
        }

    }

}
