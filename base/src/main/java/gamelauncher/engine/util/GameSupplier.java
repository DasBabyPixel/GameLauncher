package gamelauncher.engine.util;

import gamelauncher.engine.GameException;

public interface GameSupplier<T> {

	T get() throws GameException;

}
