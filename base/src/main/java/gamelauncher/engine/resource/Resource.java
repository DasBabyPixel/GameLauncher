package gamelauncher.engine.resource;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public interface Resource extends GameResource {

    /**
     * Creates a new {@link ResourceStream} for this {@link Resource}
     *
     * @return the {@link ResourceStream}
     */
    ResourceStream newResourceStream() throws GameException;

}
