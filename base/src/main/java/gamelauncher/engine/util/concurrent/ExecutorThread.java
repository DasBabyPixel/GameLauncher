package gamelauncher.engine.util.concurrent;

import java.util.concurrent.CompletableFuture;

import gamelauncher.engine.util.function.GameCallable;
import gamelauncher.engine.util.function.GameRunnable;

/**
 * This is a {@link Thread} with executor capabilities.
 * 
 * @author DasBabyPixel
 */
public interface ExecutorThread {

	/**
	 * @param runnable
	 * @return a new future for this task
	 */
	CompletableFuture<Void> submit(GameRunnable runnable);

	/**
	 * @param <T>
	 * @param callable
	 * @return a new future for this task
	 */
	default <T> CompletableFuture<T> submit(GameCallable<T> callable) {
		CompletableFuture<T> fut = new CompletableFuture<>();
		submit(() -> {
			try {
				T t = callable.call();
				fut.complete(t);
			} catch (Exception ex) {
				fut.completeExceptionally(ex);
			}
		});
		return fut;
	}

	/**
	 * Runs all submitted tasks on the current thread. DO NOT CALL THIS UNLESS YOU
	 * KNOW WHAT YOU'RE DOING!
	 */
	void workQueue();

}
