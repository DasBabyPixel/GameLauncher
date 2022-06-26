package gamelauncher.engine.render;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public abstract class Renderer {

	/**
	 * @param window
	 * @throws GameException
	 */
	public abstract void render(Window window) throws GameException;

	/**
	 * @param window
	 * @throws GameException
	 */
	public void init(Window window) throws GameException {
	}

	/**
	 * @param window
	 * @throws GameException
	 */
	public void close(Window window) throws GameException {
	}
}
