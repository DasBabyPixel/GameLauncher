package gamelauncher.engine.util.function;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 * @param <V>
 */
public interface GamePredicate<V> {
	
	/**
	 * @param v
	 * @return if the test is successful
	 * @throws GameException
	 */
	boolean test(V v) throws GameException;
	
}
