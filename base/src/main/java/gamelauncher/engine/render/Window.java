package gamelauncher.engine.render;

import gamelauncher.engine.input.Input;

public interface Window {

	void beginFrame();
	
	void endFrame();

	DrawContext getContext();
	
	Input getInput();
	
	void setFrameRenderer(FrameRenderer renderer);
	
}
