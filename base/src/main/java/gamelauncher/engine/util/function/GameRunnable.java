package gamelauncher.engine.util.function;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public interface GameRunnable {

	/**
	 * @throws GameException
	 */
	void run() throws GameException;

	/**
	 * Converts this {@link GameRunnable} to a {@link Runnable} object
	 * 
	 * @return a {@link Runnable} for this {@link GameRunnable}
	 */
	default Runnable toRunnable() {
		return () -> {
			try {
				run();
			} catch (GameException ex) {
				throw new RuntimeException(ex);
			}
		};
	}
}
