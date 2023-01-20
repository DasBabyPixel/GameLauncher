package gamelauncher.engine.render.texture;

import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;

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
	 *
	 * @return a new future
	 */
	CompletableFuture<Void> allocate(int width, int height);

	/**
	 * Resizes this texture
	 *
	 * @param width
	 * @param height
	 *
	 * @return a future for this task
	 */
	CompletableFuture<Void> resize(int width, int height);

	/**
	 * @return the width
	 */
	NumberValue width();

	/**
	 * @return the height
	 */
	NumberValue height();

	/**
	 * @param image the image to upload
	 *
	 * @return a future for this task
	 *
	 * @throws GameException an exception on error
	 */
	@Deprecated
	CompletableFuture<Void> uploadAsync(BufferedImage image) throws GameException;

	/**
	 * @param stream the stream to upload from
	 *
	 * @return a future for this task
	 *
	 * @throws GameException an exception on error
	 */
	CompletableFuture<Void> uploadAsync(ResourceStream stream) throws GameException;

	/**
	 * @param stream the stream to upload from
	 * @param x      the x-position to upload to
	 * @param y      the y-position to upload to
	 *
	 * @return a future for this task
	 *
	 * @throws GameException an exception on error
	 */
	CompletableFuture<Void> uploadSubAsync(ResourceStream stream, int x, int y)
			throws GameException;

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
	 *
	 * @return a new future
	 *
	 * @throws GameException
	 */
	CompletableFuture<Void> copyTo(Texture other, int srcX, int srcY, int dstX, int dstY, int width,
			int height) throws GameException;

}
