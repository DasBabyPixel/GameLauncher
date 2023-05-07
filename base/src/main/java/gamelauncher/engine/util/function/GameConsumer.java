package gamelauncher.engine.util.function;

import gamelauncher.engine.util.GameException;

/**
 * @param <T>
 * @author DasBabyPixel
 */
public interface GameConsumer<T> {

    /**
     * @param t
     * @throws GameException
     */
    void accept(T t) throws GameException;

}
