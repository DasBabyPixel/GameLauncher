package gamelauncher.engine.resource;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;
import gamelauncher.engine.util.function.GameSupplier;
import java8.util.concurrent.CompletableFuture;

/**
 * @author DasBabyPixel
 */
public interface GameResource {

    void storeValue(Key key, Object value);

    <T> T storedValue(Key key);

    <T> T storedValue(Key key, GameSupplier<T> defaultSupplier);

    /**
     * Cleanes up this {@link GameResource resource}
     *
     * @throws GameException an exception
     */
    void cleanup() throws GameException;

    /**
     * @return if this resource is cleaned up
     */
    boolean cleanedUp();

    /**
     * @return the future for when this object is cleaned up
     */
    CompletableFuture<Void> cleanupFuture();

}
