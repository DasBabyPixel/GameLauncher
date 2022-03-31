package gamelauncher.engine.render;

public interface GameRenderer extends FrameRenderer {

	void setRenderer(Renderer renderer);
	
	Renderer getRenderer();
	
}
