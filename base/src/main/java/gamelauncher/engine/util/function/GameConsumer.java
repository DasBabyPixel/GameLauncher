package gamelauncher.engine.util.function;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 * @param <T>
 */
public interface GameConsumer<T> {

	/**
	 * @param t
	 * @throws GameException
	 */
	void accept(T t) throws GameException;
	
}
