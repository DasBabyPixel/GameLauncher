package gamelauncher.engine.util.concurrent;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameResource;
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.engine.util.logging.Logger;

/**
 * Utility class for multithreading
 * 
 * @author DasBabyPixel
 */
public class Threads implements GameResource {

	/**
	 * Wheather or not stack traces should be calculated with causes from other
	 * threads when tasks are submitted
	 */
	public static final boolean calculateThreadStacks = Boolean.getBoolean("calculateThreadStacks");

	private final Logger logger = Logger.getLogger();

	/**
	 * A work stealing {@link Executor}.
	 * 
	 * @see Executors#newWorkStealingPool()
	 */
	public final ExecutorThreadService workStealing;

	/**
	 * A cached {@link Executor}
	 * 
	 * @see Executors#newCachedThreadPool()
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
	 * @param service
	 */
	public void shutdown(ExecutorThreadService service) {
		service.exit();
		services.remove(service);
	}

	@Override
	public void cleanup() throws GameException {
		try {
			for (ExecutorThreadService service : services) {
				CompletableFuture<Void> fut = service.exit();
				try {
					fut.get(5, TimeUnit.SECONDS);
				} catch (ExecutionException ex) {
					ex.printStackTrace();
				} catch (TimeoutException ex) {
					Collection<GameRunnable> cancelled = service.exitNow();
					logger.errorf(
							"Terminating ExecutorService (%s) took more than 5 seconds. Enforcing termination. Cancelled %s tasks",
							service.getClass().getSimpleName(), cancelled.size());
				}
			}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @see Thread#sleep(long)
	 * @param millis
	 */
	public static void sleep(long millis) {
		LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(millis));
	}

	/**
	 * Waits for the futures to finish. If this thread is an {@link ExecutorThread},
	 * {@link ExecutorThread#workQueue()} will be invoked.
	 * 
	 * @param futures
	 */
	public static void waitFor(CompletableFuture<?>... futures) {
		final Thread thread = Thread.currentThread();
		final int required = futures.length;
		final AtomicInteger done = new AtomicInteger();
		Runnable run = () -> {
			if (done.incrementAndGet() == required) {
				unpark(thread);
			}
		};
		for (CompletableFuture<?> fut : futures) {
			fut.thenRun(run);
		}
		while (done.get() != required) {
			park();
		}
	}

	/**
	 * Parks the current thread
	 */
	public static void park() {
		Thread thread = Thread.currentThread();
		if (thread instanceof ParkableThread) {
			((ParkableThread) thread).park();
		} else {
			LockSupport.park();
		}
	}

	/**
	 * Unparks the given thread. Calling this while the thread is not parked will
	 * cause the next park invocation to not be executed
	 * 
	 * @param thread
	 */
	public static void unpark(Thread thread) {
		if (thread instanceof ParkableThread) {
			unpark((ParkableThread) thread);
		} else {
			LockSupport.unpark(thread);
		}
	}

	/**
	 * Unparks the given thread. Calling this while the thread is not parked will
	 * cause the next park invocation to not be executed
	 * 
	 * @param thread
	 */
	public static void unpark(ParkableThread thread) {
		thread.unpark();
	}

	/**
	 * Waits for the future to finish. This behaves like
	 * {@link Threads#waitFor(CompletableFuture[])}
	 * 
	 * @param <T>
	 * @param future
	 * @return the result from the {@link CompletableFuture}
	 */
	public static <T> T waitFor(CompletableFuture<T> future) {
		Thread thread = Thread.currentThread();
		AtomicReference<T> ref = new AtomicReference<>();
		future.thenAccept(value -> {
			ref.set(value);
			unpark(thread);
		});
		while (!future.isDone()) {
			park();
		}
		return ref.get();
	}

	/**
	 * @return the current thread
	 * @see Thread#currentThread()
	 */
	public static Thread currentThread() {
		return Thread.currentThread();
	}

}
