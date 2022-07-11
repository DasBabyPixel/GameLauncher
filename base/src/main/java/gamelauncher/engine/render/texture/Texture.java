package gamelauncher.engine.render.texture;

import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameResource;

/**
 * @author DasBabyPixel
 */
public interface Texture extends GameResource {

	/**
	 * @return a {@link BufferedImage} for this {@link Texture}
	 */
	BufferedImage getBufferedImage();
	
	/**
	 * @param width
	 * @param height
	 */
	void allocate(int width, int height);
	
	/**
	 * @param image
	 * @return a future for this task
	 * @throws GameException 
	 */
	CompletableFuture<Void> uploadAsync(BufferedImage image) throws GameException;
	
	/**
	 * @param stream
	 * @return a future for this task
	 * @throws GameException 
	 */
	CompletableFuture<Void> uploadAsync(ResourceStream stream) throws GameException;
	
}
