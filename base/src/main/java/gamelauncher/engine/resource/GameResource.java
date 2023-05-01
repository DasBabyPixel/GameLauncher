package gamelauncher.engine.resource;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;
import gamelauncher.engine.util.function.GameSupplier;
import java8.util.concurrent.CompletableFuture;

/**
 * @author DasBabyPixel
 */
public interface GameResource {

    @Api
    void storeValue(Key key, Object value);

    @Api
    <T> T storedValue(Key key);

    @Api
    <T> T storedValue(Key key, GameSupplier<T> defaultSupplier);

    /**
     * Cleanes up this {@link GameResource resource}
     *
     * @throws GameException an exception
     */
    @Api
    void cleanup() throws GameException;

    /**
     * @return if this resource is cleaned up
     */
    @Api
    boolean cleanedUp();

    /**
     * @return the future for when this object is cleaned up
     */
    @Api
    CompletableFuture<Void> cleanupFuture();

}
