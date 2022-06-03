package gamelauncher.engine.util;

import gamelauncher.engine.GameException;

public interface GameConsumer<T> {

	void accept(T t) throws GameException;
	
}
