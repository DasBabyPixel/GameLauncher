package gamelauncher.engine.util;

import gamelauncher.engine.GameException;

public interface GameResource {

	void cleanup() throws GameException;
	
}
