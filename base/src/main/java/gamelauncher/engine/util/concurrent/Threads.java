package gamelauncher.engine.util.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.LockSupport;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameResource;
import gamelauncher.engine.util.logging.Logger;

/**
 * Utility class for multithreading
 * 
 * @author DasBabyPixel
 */
public class Threads implements GameResource {

	private final Logger logger = Logger.getLogger();

	/**
	 * A work stealing {@link Executor}.
	 * 
	 * @see Executors#newWorkStealingPool()
	 */
	public final ExecutorService workStealing;
	/**
	 * A cached {@link Executor}
	 * 
	 * @see Executors#newCachedThreadPool()
	 */
	public final ExecutorService cached;

	/**
	 * All services combined
	 */
	public final Collection<ExecutorService> services;

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
	public ExecutorService newWorkStealingPool() {
		ExecutorService service = Executors.newWorkStealingPool();
		services.add(service);
		return service;
	}

	/**
	 * @return a new cached thread pool
	 */
	public ExecutorService newCachedThreadPool() {
		ExecutorService service = Executors.newCachedThreadPool();
		services.add(service);
		return service;
	}

	/**
	 * @param service
	 */
	public void shutdown(ExecutorService service) {
		service.shutdown();
		services.remove(service);
	}

	@Override
	public void cleanup() throws GameException {
		try {
			for (ExecutorService service : services) {
				service.shutdown();
				if (!service.awaitTermination(5, TimeUnit.SECONDS)) {
					List<Runnable> cancelled = service.shutdownNow();
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
		Thread cur = Thread.currentThread();
		ExecutorThread ex = null;
		if (cur instanceof ExecutorThread) {
			ex = (ExecutorThread) cur;
		}
		for (CompletableFuture<?> fut : futures) {
			while (!fut.isDone()) {
				try {
					fut.get(5, TimeUnit.MILLISECONDS);
				} catch (InterruptedException | ExecutionException ex1) {
					ex1.printStackTrace();
				} catch (TimeoutException ex1) {
					if (ex != null) {
						ex.workQueue();
					}
				}
			}
		}
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
		waitFor(new CompletableFuture[] {
				future
		});
		return future.getNow(null);
	}
}
