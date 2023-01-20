package gamelauncher.engine.render;

import de.dasbabypixel.api.property.BooleanValue;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.input.Input;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;

import java.util.concurrent.CompletableFuture;

/**
 * @author DasBabyPixel
 */
public interface Frame extends GameResource {

	/**
	 * @return the input for this {@link Frame}
	 */
	Input input();

	/**
	 * @return the frameCloseFuture
	 */
	CompletableFuture<Frame> frameCloseFuture();

	/**
	 * @return the underlying {@link Framebuffer}
	 */
	Framebuffer framebuffer();

	/**
	 * @return the {@link RenderMode} for this {@link Frame}
	 */
	RenderMode renderMode();

	/**
	 * Sets the {@link RenderMode} for this {@link Frame}
	 *
	 * @param renderMode the {@link RenderMode}
	 */
	void renderMode(RenderMode renderMode);

	/**
	 * Sets the {@link FrameRenderer} for this {@link Frame}
	 *
	 * @param renderer the {@link FrameRenderer}
	 */
	void frameRenderer(FrameRenderer renderer);

	/**
	 * @return the fullscreen property. Modify this to set fullscreen
	 */
	BooleanValue fullscreen();

	/**
	 * @return the {@link FrameRenderer} for this {@link Frame}
	 */
	FrameRenderer frameRenderer();

	/**
	 * @return a new frame that shares resources with this {@link Frame}
	 *
	 * @throws GameException an exception
	 */
	Frame newFrame() throws GameException;

	/**
	 * @return the {@link FrameCounter} for this {@link Frame}
	 */
	FrameCounter frameCounter();

	/**
	 * Schedules a draw
	 */
	void scheduleDraw();

	/**
	 * Waits for the next frame
	 */
	void waitForFrame();

	/**
	 * Schedules a draw and waits for the next frame
	 */
	void scheduleDrawWaitForFrame();

	/**
	 * @return the launcher
	 */
	GameLauncher launcher();

}
