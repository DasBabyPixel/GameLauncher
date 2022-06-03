package gamelauncher.engine.render;

import gamelauncher.engine.GameException;

public abstract class Renderer {

	public abstract void render(Window window) throws GameException;

	@SuppressWarnings("unused")
	public void init() throws GameException {
	}

	@SuppressWarnings("unused")
	public void close() throws GameException {
	}
}
