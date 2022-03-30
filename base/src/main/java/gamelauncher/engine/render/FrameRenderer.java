package gamelauncher.engine.render;

public abstract class FrameRenderer {

	public void renderFrame(Window window) {
		renderFrame0(window);
		swapBuffers(window);
	}

	protected abstract void renderFrame0(Window window);

	protected abstract void swapBuffers(Window window);
}
