package gamelauncher.lwjgl.render;

import static org.lwjgl.opengl.GL11.*;

import java.util.concurrent.atomic.AtomicReference;

import gamelauncher.engine.GameException;
import gamelauncher.engine.render.GameRenderer;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.render.Window;

public class LWJGLGameRenderer implements GameRenderer {

	private final AtomicReference<Renderer> renderer = new AtomicReference<>();
	private Renderer crenderer;

	@Override
	public void setRenderer(Renderer renderer) {
		this.renderer.set(renderer);
	}

	@Override
	public Renderer getRenderer() {
		return this.renderer.get();
	}

	@Override
	public void renderFrame(Window window) throws GameException {
		window.beginFrame();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		Renderer renderer = this.renderer.get();
		if (renderer != crenderer) {
			if (crenderer != null) {
				crenderer.close();
			}
			if (renderer != null) {
				renderer.init();
			}
			crenderer = renderer;
		}
		if (renderer != null) {
			renderer.render(window, window.getContext());
		}
		window.endFrame();
	}
}
