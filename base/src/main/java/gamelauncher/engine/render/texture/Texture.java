package gamelauncher.engine.render.texture;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;
import java8.util.concurrent.CompletableFuture;

/**
 * @author DasBabyPixel
 */
public interface Texture extends GameResource {

	//	/**
	//	 * @return a {@link BufferedImage} for this {@link Texture}
	//	 */
	//	CompletableFuture<BufferedImage> getBufferedImage();

    /**
     * @param width  the width of the new texture
     * @param height the height of the new texture
     * @return a new future
     */
    CompletableFuture<Void> allocate(int width, int height);

    /**
     * Resizes this texture
     *
     * @param width  the width
     * @param height the height
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

	//	/**
	//	 * @param image the image to upload
	//	 * @return a future for this task
	//	 * @throws GameException an exception on error
	//	 */
	//	@Deprecated
	//	CompletableFuture<Void> uploadAsync(BufferedImage image) throws GameException;

    /**
     * @param stream the stream to upload from
     * @return a future for this task
     * @throws GameException an exception on error
     */
    CompletableFuture<Void> uploadAsync(ResourceStream stream) throws GameException;

    /**
     * @param stream the stream to upload from
     * @param x      the x-position to upload to
     * @param y      the y-position to upload to
     * @return a future for this task
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
     * @return a new future
     * @throws GameException
     */
    CompletableFuture<Void> copyTo(Texture other, int srcX, int srcY, int dstX, int dstY, int width,
                                   int height) throws GameException;

}
