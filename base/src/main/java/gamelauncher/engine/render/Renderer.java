package gamelauncher.engine.render;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public abstract class Renderer {

    /**
     * @throws GameException
     */
    public abstract void render() throws GameException;

    /**
     * @throws GameException
     */
    public void init() throws GameException {
    }

    /**
     * @throws GameException
     */
    public void cleanup() throws GameException {
    }
}
