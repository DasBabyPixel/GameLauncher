package gamelauncher.engine.util;

import gamelauncher.engine.GameException;

public interface GameCloseable {

	void close() throws GameException;
	
}
