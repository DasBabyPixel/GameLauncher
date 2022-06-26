package gamelauncher.engine.util.function;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 *
 */
public interface GameResource {

	/**
	 * Cleanes up this {@link GameResource resource}
	 * 
	 * @throws GameException
	 */
	void cleanup() throws GameException;

}
