package gamelauncher.engine.render;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public interface FrameRenderer {

	/**
	 * Called when a frame is rendered
	 * 
	 * @param frame
	 * @throws GameException
	 */
	void renderFrame(Frame frame) throws GameException;

	/**
	 * Called when the size of the window is changed
	 * 
	 * @param frame
	 * @throws GameException
	 */
	void windowSizeChanged(Frame frame) throws GameException;

	/**
	 * Called when this {@link FrameRenderer} is initialized
	 * 
	 * @param frame
	 * @throws GameException
	 */
	void init(Frame frame) throws GameException;

	/**
	 * Called when this {@link FrameRenderer} is cleaned up
	 * 
	 * @param frame
	 * @throws GameException
	 */
	void cleanup(Frame frame) throws GameException;

	/**
	 * Refreshes this display. Used for when the current content is already rendered
	 * to an off-screen framebuffer, but the on-screen framebuffer is not
	 * up-to-date. In that case we only have to update the on-screen framebuffer to
	 * contain the contents of the off-screen framebuffer.
	 * 
	 * Basically, its only used for the main {@link FrameRenderer}, cause all the
	 * others dont have synchronization needs
	 * 
	 * @param frame
	 * @throws GameException
	 */
	void refreshDisplay(Frame frame) throws GameException;

}
