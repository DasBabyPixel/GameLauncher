package gamelauncher.engine.render;

import de.dasbabypixel.annotations.Api;
import de.dasbabypixel.api.property.BooleanValue;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.input.Input;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import java8.util.concurrent.CompletableFuture;

/**
 * @author DasBabyPixel
 */
@Api
public interface Frame extends GameResource {

    /**
     * @return the input for this {@link Frame}
     */
    @Api Input input();

    /**
     * @return the frameCloseFuture
     */
    @Api CompletableFuture<Frame> frameCloseFuture();

    /**
     * @return the underlying {@link Framebuffer}
     */
    @Api Framebuffer framebuffer();

    /**
     * @return the {@link RenderMode} for this {@link Frame}
     */
    @Api RenderMode renderMode();

    /**
     * Sets the {@link RenderMode} for this {@link Frame}
     *
     * @param renderMode the {@link RenderMode}
     */
    @Api void renderMode(RenderMode renderMode);

    /**
     * Sets the {@link FrameRenderer} for this {@link Frame}
     *
     * @param renderer the {@link FrameRenderer}
     */
    @Api void frameRenderer(FrameRenderer renderer);

    /**
     * @return the fullscreen property. Modify this to set fullscreen
     */
    @Api BooleanValue fullscreen();

    /**
     * @return the {@link FrameRenderer} for this {@link Frame}
     */
    @Api FrameRenderer frameRenderer();

    @Api ExecutorThread renderThread();

    /**
     * @return a new frame that shares resources with this {@link Frame}
     * @throws GameException an exception
     */
    @Api Frame newFrame() throws GameException;

    /**
     * @return the {@link FrameCounter} for this {@link Frame}
     */
    @Api FrameCounter frameCounter();

    /**
     * Schedules a draw
     */
    @Api void scheduleDraw();

    /**
     * Waits for the next frame
     */
    @Api void waitForFrame();

    /**
     * Schedules a draw and waits for the next frame
     */
    @Api void scheduleDrawWaitForFrame();

    /**
     * @return the launcher
     */
    @Api GameLauncher launcher();

}
