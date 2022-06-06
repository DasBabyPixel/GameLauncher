package gamelauncher.engine.render;

import gamelauncher.engine.GameException;

public interface FrameRenderer {

	void renderFrame(Window window) throws GameException;
	
	void windowSizeChanged(Window window) throws GameException;

	void init(Window window) throws GameException;

	void close(Window window) throws GameException;
}
