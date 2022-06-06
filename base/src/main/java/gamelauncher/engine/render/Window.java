package gamelauncher.engine.render;

import gamelauncher.engine.input.Input;

public interface Window {

	void beginFrame();
	
	void endFrame();

	DrawContext getContext();
	
	int getFramebufferWidth();
	
	int getFramebufferHeight();
	
	Input getInput();
	
	void setFrameRenderer(FrameRenderer renderer);
	
}
