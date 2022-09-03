package gamelauncher.engine.render;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public interface FrameRenderer {

	/**
	 * Called when a frame is rendered
	 * 
	 * @param window
	 * @throws GameException
	 */
	void renderFrame(Window window) throws GameException;

	/**
	 * Called when the size of the window is changed
	 * 
	 * @param window
	 * @throws GameException
	 */
	void windowSizeChanged(Window window) throws GameException;

	/**
	 * Called when this {@link FrameRenderer} is initialized
	 * 
	 * @param window
	 * @throws GameException
	 */
	void init(Window window) throws GameException;

	/**
	 * Called when this {@link FrameRenderer} is cleaned up
	 * 
	 * @param window
	 * @throws GameException
	 */
	void cleanup(Window window) throws GameException;

	/**
	 * Refreshes this display. Used for when the current content is already rendered
	 * to an off-screen framebuffer, but the on-screen framebuffer is not
	 * up-to-date. In that case we only have to update the on-screen framebuffer to
	 * contain the contents of the off-screen framebuffer
	 * 
	 * @param window
	 * @throws GameException
	 */
	void refreshDisplay(Window window) throws GameException;

}
