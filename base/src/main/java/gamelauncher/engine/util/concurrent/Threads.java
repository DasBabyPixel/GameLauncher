/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.concurrent;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.WrapperExecutorThreadService.WrapperCallable;
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.engine.util.logging.Logger;
import java8.util.concurrent.CompletableFuture;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * Utility class for multithreading
 *
 * @author DasBabyPixel
 */
public class Threads extends AbstractGameResource {

    /**
     * Wheather or not stack traces should be calculated with causes from other threads when tasks
     * are submitted
     */
    public static final boolean calculateThreadStacks = Boolean.getBoolean("calculateThreadStacks");

    private static final Logger logger = Logger.logger();

    /**
     * A work stealing {@link Executor}.
     *
     * @see Executors#newWorkStealingPool()
     */
    public final ExecutorThreadService workStealing;

    /**
     * A cached {@link java.util.concurrent.Executor}
     *
     * @see java.util.concurrent.Executors#newCachedThreadPool()
     */
    public final ExecutorThreadService cached;

    /**
     * All services combined
     */
    public final Collection<ExecutorThreadService> services;

    /**
     *
     */
    public Threads() {
        services = new CopyOnWriteArrayList<>();
        cached = newCachedThreadPool();
        workStealing = newWorkStealingPool();
    }

    /**
     * @return the stacktrace origin of thread-submitted tasks. DOES NOT HAVE THE STACKTRACE OF THE
     * CURRENT THREAD!!!
     */
    public static GameException buildStacktrace() {
        Thread cur = Thread.currentThread();
        if (cur instanceof AbstractExecutorThread) {
            return ((AbstractExecutorThread) cur).buildStacktrace();
        }
        WrapperCallable<?> c;
        if ((c = WrapperCallable.threadLocal.get()) != null) {
            return c.buildStacktrace();
        }
        return new GameException();
    }

    /**
     * @param millis the millis to sleep for
     * @see LockSupport#parkNanos(long)
     */
    public static void sleep(long millis) {
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(millis));
    }

    @Api public static void whenAllComplete(CompletableFuture<?>[] futures, GameRunnable runnable) {
        AtomicInteger countdown = new AtomicInteger(futures.length);
        for (CompletableFuture<?> future : futures) {
            future.thenRun(() -> {
                if (countdown.decrementAndGet() == 0) {
                    runnable.toRunnable().run();
                }
            });
        }
    }

    @Api public static void whenAllComplete(Collection<? extends CompletableFuture<?>> futures, GameRunnable runnable) {
        AtomicInteger countdown = new AtomicInteger(futures.size());
        for (CompletableFuture<?> future : futures) {
            future.thenRun(() -> {
                if (countdown.decrementAndGet() == 0) {
                    runnable.toRunnable().run();
                }
            });
        }
    }

    /**
     * Waits for a future.
     *
     * @param future
     */
    @Api public static <T> T await(CompletableFuture<T> future) throws GameException {
        try {
            return future.get();
        } catch (InterruptedException | CancellationException e) {
            throw GameException.wrap(e);
        } catch (ExecutionException e) {
            throw GameException.wrap(e.getCause());
        }
    }

    /**
     * Parks the current thread
     */
    public static void park() {
        LockSupport.park();
    }

    /**
     * Parks the current thread for a specified amount of nanoseconds
     *
     * @param nanos the nanos to park for
     */
    public static void park(long nanos) {
        LockSupport.parkNanos(nanos);
    }

    /**
     * Unparks the given thread. Calling this while the thread is not parked will cause the next
     * park invocation to not be executed
     *
     * @param thread the thread to unpark
     */
    public static void unpark(Thread thread) {
        LockSupport.unpark(thread);
    }

    /**
     * @return the current thread
     * @see Thread#currentThread()
     */
    public static Thread currentThread() {
        return Thread.currentThread();
    }

    /**
     * @return a new work stealing pool
     */
    public ExecutorThreadService newWorkStealingPool() {
        ExecutorThreadService service = new WrapperExecutorThreadService(Executors.newWorkStealingPool());
        services.add(service);
        return service;
    }

    /**
     * @return a new cached thread pool
     */
    public ExecutorThreadService newCachedThreadPool() {
        ExecutorThreadService service = new WrapperExecutorThreadService(Executors.newCachedThreadPool());
        services.add(service);
        return service;
    }

    /**
     * @param service the service to shut down
     */
    @Api public void shutdown(ExecutorThreadService service) {
        service.exit(); // TODO check if services are correctly shut down
        services.remove(service);
    }

    @Override public CompletableFuture<Void> cleanup0() throws GameException {
        try {
            for (ExecutorThreadService service : services) {
                CompletableFuture<Void> fut = service.exit();
                try {
                    fut.get(5, TimeUnit.SECONDS);
                } catch (ExecutionException ex) {
                    logger.error(ex);
                } catch (TimeoutException ex) {
                    Collection<GameRunnable> cancelled = service.exitNow();
                    logger.errorf("Terminating ExecutorService (%s) took more than 5 seconds. Enforcing termination. Cancelled %s tasks", service.getClass().getSimpleName(), cancelled.size());
                }
            }
        } catch (InterruptedException ex) {
            logger.error(ex);
        }
        return null;
    }

}
