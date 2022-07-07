package gamelauncher.engine.util.function;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 * @param <T>
 */
public interface GameSupplier<T> {

	/**
	 * @return the object supplied by this {@link GameSupplier}
	 * @throws GameException
	 */
	T get() throws GameException;

}
