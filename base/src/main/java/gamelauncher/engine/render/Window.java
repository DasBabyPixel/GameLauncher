package gamelauncher.engine.render;

public interface Window {

	void beginFrame();
	
	void endFrame();

	DrawContext getContext();
	
	void setFrameRenderer(FrameRenderer renderer);
	
}
