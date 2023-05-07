package gamelauncher.engine.util.function;

import gamelauncher.engine.util.GameException;

/**
 * @param <V>
 * @author DasBabyPixel
 */
public interface GamePredicate<V> {

    /**
     * @param v
     * @return if the test is successful
     * @throws GameException
     */
    boolean test(V v) throws GameException;

}
