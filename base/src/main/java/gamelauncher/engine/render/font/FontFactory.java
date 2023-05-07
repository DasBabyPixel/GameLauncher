package gamelauncher.engine.render.font;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.resource.Resource;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
@Api
public interface FontFactory {

    /**
     * This method will <B>NOT</B> close the given {@link ResourceStream}!
     *
     * @param stream the {@link ResourceStream} to load the font from
     * @return a {@link Font} created with the given {@link ResourceStream}
     * @throws GameException an exception
     */
    @Api default Font createFont(ResourceStream stream) throws GameException {
        return createFont(stream, false);
    }

    /**
     * @param resource the {@link Resource to load the font from}
     * @return a {@link Font} created with the given {@link Resource}
     * @throws GameException an exception
     */
    @Api default Font createFont(Resource resource) throws GameException {
        return createFont(resource.newResourceStream(), true);
    }

    /**
     * @param stream the stream to load the font from
     * @param close  whether the stream should be closed after loading the font
     * @return a font created with the given {@link ResourceStream}
     * @throws GameException an exception
     */
    @Api Font createFont(ResourceStream stream, boolean close) throws GameException;

}
