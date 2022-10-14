package gamelauncher.engine.resource;

import java.util.concurrent.CompletableFuture;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 *
 */
public interface GameResource {

	/**
	 * Cleanes up this {@link GameResource resource}
	 * 
	 * @throws GameException
	 */
	void cleanup() throws GameException;
	
	/**
	 * @return if this resource is cleaned up
	 */
	boolean isCleanedUp();
	
	/**
	 * @return the future for when this object is cleaned up
	 */
	CompletableFuture<Void> cleanupFuture();

}
