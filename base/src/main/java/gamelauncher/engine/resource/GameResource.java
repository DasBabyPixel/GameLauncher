package gamelauncher.engine.resource;

import gamelauncher.engine.util.GameException;

import java.util.concurrent.CompletableFuture;

/**
 * @author DasBabyPixel
 */
public interface GameResource {

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
