package game.engine.render;

public abstract class Renderer {

	public abstract void render(Window window) throws RenderException;

	@SuppressWarnings("unused")
	public void init() throws RenderException {
	}

	@SuppressWarnings("unused")
	public void close() throws RenderException {
	}
}
