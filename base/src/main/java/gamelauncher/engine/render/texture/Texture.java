package gamelauncher.engine.render.texture;

import de.dasbabypixel.annotations.Api;
import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;
import java8.util.concurrent.CompletableFuture;

import java.nio.ByteBuffer;

/**
 * @author DasBabyPixel
 */
@Api
public interface Texture extends GameResource {

    /**
     * @return a future of the pixels contained in this texture. The format is RGBA_8
     */
    @Api CompletableFuture<ByteBuffer> queryPixels();

    /**
     * Sets the filter for the specified {@link TextureFilter.FilterType}
     *
     * @param filterType
     * @param filter
     */
    @Api void filter(TextureFilter.FilterType filterType, TextureFilter.Filter filter);

    /**
     * @param filterType
     * @return the filter for the specified {@link TextureFilter.FilterType}
     */
    @Api TextureFilter.Filter filter(TextureFilter.FilterType filterType);

    /**
     * @param width  the width of the new texture
     * @param height the height of the new texture
     * @return a new future
     */
    @Api CompletableFuture<Void> allocate(int width, int height);

    /**
     * Resizes this texture
     *
     * @param width  the width
     * @param height the height
     * @return a future for this task
     */
    @Api CompletableFuture<Void> resize(int width, int height);

    /**
     * @return the width
     */
    @Api NumberValue width();

    /**
     * @return the height
     */
    @Api NumberValue height();

    /**
     * @param stream the stream to upload from
     * @return a future for this task
     * @throws GameException an exception on error
     */
    @Api CompletableFuture<Void> uploadAsync(ResourceStream stream) throws GameException;

    /**
     * @param stream the stream to upload from
     * @param x      the x-position to upload to
     * @param y      the y-position to upload to
     * @return a future for this task
     * @throws GameException an exception on error
     */
    @Api CompletableFuture<Void> uploadSubAsync(ResourceStream stream, int x, int y) throws GameException;

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
    @Api CompletableFuture<Void> copyTo(Texture other, int srcX, int srcY, int dstX, int dstY, int width, int height) throws GameException;

}
