package gamelauncher.engine.render;

import gamelauncher.engine.GameException;

public abstract class Renderer {

	public abstract void render(Window window) throws GameException;

	@SuppressWarnings("unused")
	public void init(Window window) throws GameException {
	}

	@SuppressWarnings("unused")
	public void close(Window window) throws GameException {
	}
}
