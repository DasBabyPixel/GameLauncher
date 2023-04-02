package gamelauncher.engine.util.concurrent;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import gamelauncher.engine.util.function.GameRunnable;

/**
 * @author DasBabyPixel
 */
public interface ExecutorThreadService extends ExecutorThread {

	/**
	 * @return the exitfuture
	 */
	CompletableFuture<Void> exit();
	
	/**
	 * @return the cancelled runnables
	 */
	Collection<GameRunnable> exitNow();

	Executor executor();
	
	/**
	 * @return the exitfuture
	 */
	CompletableFuture<Void> exitFuture();
	
}
