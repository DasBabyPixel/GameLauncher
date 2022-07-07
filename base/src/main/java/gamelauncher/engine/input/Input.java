package gamelauncher.engine.input;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public interface Input {
	
	/**
	 * Handles the Input queue and fires all events
	 * @throws GameException 
	 */
	void handleInput() throws GameException;
	
}
