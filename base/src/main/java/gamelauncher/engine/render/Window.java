package gamelauncher.engine.render;

import java.util.concurrent.CompletableFuture;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.input.Input;

/**
 * @author DasBabyPixel
 *
 */
public interface Window {

	/**
	 * Begins a frame
	 */
	void beginFrame();

	/**
	 * Ends a frame
	 */
	void endFrame();

	/**
	 * @return the framebuffer of this window
	 */
	@Deprecated
	Framebuffer getFramebuffer();

	/**
	 * @return the {@link RenderThread} for this window
	 */
	RenderThread getRenderThread();

	/**
	 * @return the {@link RenderMode} of this window
	 */
	RenderMode getRenderMode();

	/**
	 * Sets the {@link RenderMode} of this window
	 * 
	 * @param mode
	 */
	void setRenderMode(RenderMode mode);
	
	/**
	 * Schedules a draw
	 */
	void scheduleDraw();
	
	/**
	 * Waits for the next frame
	 */
	void waitForFrame();
	
	/**
	 * @return the {@link FrameCounter} for this window
	 */
	FrameCounter getFrameCounter();
	
	/**
	 * Schedules a draw and waits for the next frmae
	 */
	void scheduleDrawAndWaitForFrame();

	/**
	 * @return the input for this window
	 */
	Input getInput();

	/**
	 * @return the windowCloseFuture
	 */
	CompletableFuture<Window> windowCloseFuture();

	/**
	 * Sets the {@link FrameRenderer} for this window
	 * 
	 * @param renderer
	 */
	void setFrameRenderer(FrameRenderer renderer);
	
	/**
	 * @return the {@link FrameRenderer} for this window
	 */
	FrameRenderer getFrameRenderer();

	/**
	 * @return the {@link GameLauncher}
	 */
	GameLauncher getLauncher();

}
