package gamelauncher.engine.util;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
	public final ExecutorService workStealing = Executors.newWorkStealingPool();
	/**
	 * A cached {@link Executor}
	 * 
	 * @see Executors#newCachedThreadPool()
	 */
	public final ExecutorService cached = Executors.newCachedThreadPool();

	@Override
	public void cleanup() throws GameException {
		workStealing.shutdown();
		cached.shutdown();
		try {
			if (!workStealing.awaitTermination(5, TimeUnit.SECONDS)) {
				List<Runnable> r = workStealing.shutdownNow();
				logger.infof(
						"Terminating work stealing thread pool took more than 5 seconds. Enforcing termination: %s tasks terminated",
						r.size());
			}
			if (!cached.awaitTermination(5, TimeUnit.SECONDS)) {
				List<Runnable> r = cached.shutdownNow();
				logger.infof(
						"Terminating work cached thread pool took more than 5 seconds. Enforcing termination: %s tasks terminated",
						r.size());
			}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
}
