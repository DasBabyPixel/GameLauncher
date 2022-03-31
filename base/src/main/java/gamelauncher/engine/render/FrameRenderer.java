package gamelauncher.engine.render;

import gamelauncher.engine.GameException;

public interface FrameRenderer {

	void renderFrame(Window window) throws GameException;
}
