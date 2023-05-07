package gamelauncher.engine.render;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public abstract class Renderer {

    /**
     * @param framebuffer
     * @throws GameException
     */
    public abstract void render(Framebuffer framebuffer) throws GameException;

    /**
     * @param framebuffer
     * @throws GameException
     */
    public void init(Framebuffer framebuffer) throws GameException {
    }

    /**
     * @param framebuffer
     * @throws GameException
     */
    public void cleanup(Framebuffer framebuffer) throws GameException {
    }
}
