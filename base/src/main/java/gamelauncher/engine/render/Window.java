package gamelauncher.engine.render;

import java.util.concurrent.CompletableFuture;

import gamelauncher.engine.input.Input;

public interface Window {

	void beginFrame();
	
	void endFrame();

	DrawContext getContext();
	
	int getFramebufferWidth();
	
	int getFramebufferHeight();
	
	Input getInput();
	
	CompletableFuture<Window> windowCloseFuture();
	
	void setFrameRenderer(FrameRenderer renderer);
	
}
