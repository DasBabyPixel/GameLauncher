package game;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.List;

import game.render.RenderMode;
import game.render.Renderer;
import game.render.Window;
import game.resource.EmbedResourceLoader;
import game.util.logging.Logger;

public class Game {

	public Window window;
	public final GameRenderer gameRenderer = new GameRenderer();

	public void start() {
		if (window != null) {
			return;
		}

		new EmbedResourceLoader().set();

		Logger logger = Logger.getLogger(Game.class);
		System.setOut(logger.createPrintStream());
		Object[] o = new Object[] {
				"t1", 4, "s5"
		};
		List<String> list = new ArrayList<>();
		list.add("t1");
		list.add("t5");
		list.add("t9");
		logger.info(o);
		logger.info(list);

		window = new Window(400, 400, "Game");
		gameRenderer.setRenderer(new Renderer() {
			private double r1 = 0.0, g1 = 0.3, b1 = 0.6;
			private double r2 = 0.5, g2 = 0.8, b2 = 0.1;
			private double r3 = 0.8, g3 = 0.1, b3 = 0.9;

			@Override
			public void render(Window window) {
				glBegin(GL_TRIANGLES);

				double min = -(Math.sin(Math.toRadians(System.currentTimeMillis() / 20)) + 1) / 3 - 0.3;

				glVertex2d(min, -0.5);
				glColor3d(r1, g1, b1);
				glVertex2d(0, 0.5);
				glColor3d(r2, g2, b2);
				glVertex2d(-min, -0.5);
				glColor3d(r3, g3, b3);
				glEnd();
			}
		});

		window.frameRenderer.set(gameRenderer);
		window.renderLater(() -> {
			glClearColor(.2F, .2F, .2F, .8F);
		});
		window.setRenderMode(RenderMode.CONTINUOUSLY);

		window.createWindow();
		window.startRendering();
		window.show();
		window.setFloating(true);

		window.getFrameCounter().ifPresent(f -> {
			f.limit(60);
		});
		while (!window.isClosed()) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
}
