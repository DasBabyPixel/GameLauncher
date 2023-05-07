package gamelauncher.engine.render.texture;

import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public interface TextureManager extends GameResource {

    /**
     * @return a new {@link Texture}
     * @throws GameException an exception
     */
    Texture createTexture() throws GameException;

}
