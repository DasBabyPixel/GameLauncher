package game;

import static org.lwjgl.opengl.GL11.*;

import java.util.concurrent.atomic.AtomicReference;

import game.render.FrameRenderer;
import game.render.Renderer;
import game.render.Window;

public class GameRenderer extends FrameRenderer {

	private final AtomicReference<Renderer> renderer = new AtomicReference<>();

	public void setRenderer(Renderer renderer) {
		this.renderer.set(renderer);
	}

	@Override
	protected void renderFrame0(Window window) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		Renderer renderer = this.renderer.get();
		if (renderer != null) {
			renderer.render(window);
		}
	}
}
