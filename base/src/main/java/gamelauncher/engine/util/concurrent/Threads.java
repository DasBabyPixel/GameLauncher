package gamelauncher.engine.util.concurrent;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.WrapperExecutorThreadService.WrapperCallable;
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.engine.util.logging.Logger;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
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
	 *
	 * @see Thread#sleep(long)
	 */
	public static void sleep(long millis) {
		LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(millis));
	}

	/**
	 * Waits for the futures to finish. If this thread is an {@link ExecutorThread},
	 * {@link ExecutorThread#workQueue()} will be invoked.
	 *
	 * @param futures the futures to wait for
	 *
	 * @throws GameException an exception
	 */
	public static void waitFor(CompletableFuture<?>... futures) throws GameException {
		final Thread thread = Thread.currentThread();
		final int required = futures.length;
		final AtomicInteger done = new AtomicInteger();
		final AtomicReference<Throwable> ex = new AtomicReference<>(null);
		Runnable run = () -> {
			if (done.incrementAndGet() == required) {
				unpark(thread);
			}
		};
		for (CompletableFuture<?> fut : futures) {
			fut.exceptionally(th -> {
				ex.set(th);
				logger.error(th);
				done.addAndGet(required);
				unpark(thread);
				return null;
			});
			fut.thenRun(run);
		}
		while (done.get() < required) {
			park();
		}
		Throwable th = ex.get();
		if (th != null) {
			GameException e = new GameException();
			e.initCause(th);
			throw e;
		}
	}

	/**
	 * Waits for the future to finish. This behaves like
	 * {@link Threads#waitFor(CompletableFuture[])}
	 *
	 * @param future the future to wait for
	 *
	 * @return the result from the {@link CompletableFuture}
	 *
	 * @throws GameException an exception
	 */
	public static <T> T waitFor(CompletableFuture<T> future) throws GameException {
		Thread thread = Thread.currentThread();
		AtomicReference<T> ref = new AtomicReference<>();
		AtomicReference<Throwable> ex = new AtomicReference<>(null);
		future.exceptionally(th -> {
			ex.set(th);
			unpark(thread);
			return null;
		}).thenAccept(value -> {
			ref.set(value);
			unpark(thread);
		});
		while (!future.isDone()) {
			park();
		}
		Throwable th = ex.get();
		if (th != null) {
			GameException e = new GameException();
			e.initCause(th);
			throw e;
		}
		return ref.get();
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
	 * Parks the current thread for a specified amount of nanoseconds
	 *
	 * @param nanos the nanos to park for
	 */
	public static void park(long nanos) {
		Thread thread = Thread.currentThread();
		if (thread instanceof ParkableThread) {
			((ParkableThread) thread).park(nanos);
		} else {
			LockSupport.parkNanos(nanos);
		}
	}

	/**
	 * Unparks the given thread. Calling this while the thread is not parked will cause the next
	 * park invocation to not be executed
	 *
	 * @param thread the thread to unpark
	 */
	public static void unpark(Thread thread) {
		if (thread instanceof ParkableThread) {
			unpark((ParkableThread) thread);
		} else {
			LockSupport.unpark(thread);
		}
	}

	/**
	 * Unparks the given thread. Calling this while the thread is not parked will cause the next
	 * park invocation to not be executed
	 *
	 * @param thread the thread to unpark
	 */
	public static void unpark(ParkableThread thread) {
		thread.unpark();
	}

	/**
	 * @return the current thread
	 *
	 * @see Thread#currentThread()
	 */
	public static Thread currentThread() {
		return Thread.currentThread();
	}

	/**
	 * @return a new work stealing pool
	 */
	public ExecutorThreadService newWorkStealingPool() {
		ExecutorThreadService service =
				new WrapperExecutorThreadService(Executors.newWorkStealingPool());
		services.add(service);
		return service;
	}

	/**
	 * @return a new cached thread pool
	 */
	public ExecutorThreadService newCachedThreadPool() {
		ExecutorThreadService service =
				new WrapperExecutorThreadService(Executors.newCachedThreadPool());
		services.add(service);
		return service;
	}

	/**
	 * @param service the service to shutdown
	 */
	public void shutdown(ExecutorThreadService service) {
		service.exit();
		services.remove(service);
	}

	@Override
	public void cleanup0() throws GameException {
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

}
