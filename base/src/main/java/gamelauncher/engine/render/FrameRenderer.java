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
}
