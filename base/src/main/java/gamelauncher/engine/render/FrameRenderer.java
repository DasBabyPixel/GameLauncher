package gamelauncher.engine.render;

import gamelauncher.engine.GameException;

public interface FrameRenderer {

	void renderFrame(Window window) throws GameException;

	void init() throws GameException;

	void close() throws GameException;
}
