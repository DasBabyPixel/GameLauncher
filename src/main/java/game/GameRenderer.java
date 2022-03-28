package game;

import static org.lwjgl.opengl.GL11.*;

import java.util.concurrent.atomic.AtomicReference;

import game.render.FrameRenderer;
import game.render.RenderException;
import game.render.Renderer;
import game.render.Window;

public class GameRenderer extends FrameRenderer {

	private final AtomicReference<Renderer> renderer = new AtomicReference<>();
	private Renderer crenderer;

	public void setRenderer(Renderer renderer) {
		this.renderer.set(renderer);
	}

	@Override
	protected void renderFrame0(Window window) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		Renderer renderer = this.renderer.get();
		if (renderer != crenderer) {
			if (crenderer != null) {
				try {
					crenderer.close();
				} catch (RenderException ex) {
					ex.printStackTrace();
				}
			}
			if (renderer != null) {
				try {
					renderer.init();
				} catch (RenderException ex) {
					ex.printStackTrace();
				}
			}
			crenderer = renderer;
		}
		if (renderer != null) {
			try {
				renderer.render(window);
			} catch (RenderException ex) {
				ex.printStackTrace();
			}
		}
	}
}
