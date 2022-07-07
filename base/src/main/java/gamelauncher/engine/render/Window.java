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

//	/**
//	 * @return the default {@link DrawContext} of this window. Should not be
//	 *         modified
//	 */
//	DrawContext getContext();
	
	/**
	 * @return the framebuffer of this window
	 */
	Framebuffer getFramebuffer();

	/**
//	 * @return the framebuffer width property
//	 */
//	NumberValue framebufferWidth();
//
//	/**
//	 * @return the framebuffer height property
//	 */
//	NumberValue framebufferHeight();

	/**
	 * @return the {@link RenderThread} for this window
	 */
	RenderThread getRenderThread();

	/**
//	 * @return the framebuffer width
//	 */
//	int getFramebufferWidth();
//
//	/**
//	 * @return the framebuffer height
//	 */
//	int getFramebufferHeight();

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
	 * Sets the {@link FrameRenderer} of this window
	 * 
	 * @param renderer
	 */
	void setFrameRenderer(FrameRenderer renderer);

	/**
	 * @return the {@link GameLauncher}
	 */
	GameLauncher getLauncher();

}
