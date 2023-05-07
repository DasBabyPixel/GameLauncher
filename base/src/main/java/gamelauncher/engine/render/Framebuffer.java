package gamelauncher.engine.render;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.resource.GameResource;

/**
 * @author DasBabyPixel
 */
public interface Framebuffer extends GameResource {

    /**
     * Called before every frame
     */
    void beginFrame();

    /**
     * Called after every frame
     */
    void endFrame();

    /**
     * @return the width property
     */
    NumberValue width();

    /**
     * @return the height property
     */
    NumberValue height();

    /**
     * @return the rendering thread for this {@link Framebuffer}
     */
    RenderThread renderThread();

    /**
     * @return the scissorStack
     */
    ScissorStack scissorStack();

    /**
     *
     */
    void scheduleRedraw();

}
