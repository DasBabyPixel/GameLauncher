package gamelauncher.engine.util.function;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 * @param <T>
 * @param <V>
 */
public interface GameFunction<T, V> {

	/**
	 * @param t
	 * @return the object returned by this function
	 * @throws GameException
	 */
	V apply(T t) throws GameException;
	
}
