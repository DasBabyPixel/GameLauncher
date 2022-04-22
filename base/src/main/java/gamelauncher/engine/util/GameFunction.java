package gamelauncher.engine.util;

import gamelauncher.engine.GameException;

public interface GameFunction<T, V> {

	V apply(T t) throws GameException;
	
}
