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
	CompletableFuture<BufferedImage> getBufferedImage();

	/**
	 * @param width
	 * @param height
	 * @return a new future
	 */
	CompletableFuture<Void> allocate(int width, int height);

	/**
	 * Resizes this texture
	 * 
	 * @param width
	 * @param height
	 * @return a future for this task
	 */
	CompletableFuture<Void> resize(int width, int height);
	
	/**
	 * @return the width
	 */
	int getWidth();
	
	/**
	 * @return the height
	 */
	int getHeight();

	/**
	 * @param image
	 * @return a future for this task
	 * @throws GameException
	 */
	@Deprecated
	CompletableFuture<Void> uploadAsync(BufferedImage image) throws GameException;

	/**
	 * @param stream
	 * @return a future for this task
	 * @throws GameException
	 */
	CompletableFuture<Void> uploadAsync(ResourceStream stream) throws GameException;

	/**
	 * @param stream
	 * @param x
	 * @param y
	 * @return a future for this task
	 * @throws GameException
	 */
	CompletableFuture<Void> uploadSubAsync(ResourceStream stream, int x, int y) throws GameException;

	/**
	 * Copies the contents of this texture to another
	 * 
	 * @param other
	 * @param srcX 
	 * @param srcY 
	 * @param dstX 
	 * @param dstY 
	 * @param width 
	 * @param height 
	 * @return a new future
	 * @throws GameException
	 */
	CompletableFuture<Void> copyTo(Texture other, int srcX, int srcY, int dstX, int dstY, int width, int height) throws GameException;

}
