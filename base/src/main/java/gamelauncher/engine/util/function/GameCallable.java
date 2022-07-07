package gamelauncher.engine.util.function;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 * @param <T>
 */
public interface GameCallable<T> {

	/**
	 * @return T
	 * @throws GameException
	 */
	T call() throws GameException;

}
