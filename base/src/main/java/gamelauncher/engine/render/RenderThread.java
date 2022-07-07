package gamelauncher.engine.render;

import java.util.concurrent.CompletableFuture;

import gamelauncher.engine.util.function.GameCallable;
import gamelauncher.engine.util.function.GameRunnable;

/**
 * @author DasBabyPixel
 */
public interface RenderThread {

	/**
	 * @return the window of this {@link RenderThread}
	 */
	Window getWindow();
	
	/**
	 * @return the name of this {@link Thread}
	 */
	String getName();
	
	/**
	 * @param runnable
	 * @return a completionFuture
	 */
	CompletableFuture<Void> runLater(GameRunnable runnable);
	
	/**
	 * @param <T>
	 * @param callable
	 * @return a completionFuture
	 */
	<T> CompletableFuture<T> runLater(GameCallable<T> callable);
	
}
