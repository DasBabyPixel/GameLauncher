package game;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import game.render.RenderMode;
import game.render.Renderer;
import game.render.Window;
import game.render.shader.ShaderLoader;
import game.render.shader.ShaderProgram.Shader;
import game.resource.EmbedResourceLoader;
import game.resource.ResourcePath;
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
			@Override
			public void render(Window window) {
				glBegin(GL_TRIANGLES);

				double min = -(Math.sin(Math.toRadians(System.currentTimeMillis() / 20)) + 1) / 2.4 - 0.1;
//				System.out.printf("test");
//				System.out.printf("test2");
//				System.out.printf("test3%n");

				glVertex2d(min, -0.5);
				glColor3d(-min, 0, 1);
				glVertex2d(0, 0.5);
				glColor3d(1, 0, -min);
				glVertex2d(-min, -0.5);
				glColor3d(0, 1, -min);
				glEnd();
			}
		});

		window.frameRenderer.set(gameRenderer);
		window.renderLater(() -> {
			glClearColor(.2F, .2F, .2F, .8F);
			try {
				Shader shader = ShaderLoader.loadShader(Shader.Type.VERTEX, new ResourcePath("vertex.glsl"));

				shader.delete();
			} catch (IOException ex1) {
				ex1.printStackTrace();
			}
		});
		window.setRenderMode(RenderMode.CONTINUOUSLY);

		window.createWindow();
		window.startRendering();
		window.show();
		window.setFloating(true);

//		Consumer<FrameCounter> c = new Consumer<FrameCounter>() {
//			int fps = 0;
//
//			@Override
//			public void accept(FrameCounter t) {
//				if (fps != t.getFps()) {
//					fps = t.getFps();
//					System.out.println(fps);
//				}
//			}
//		};
		window.getFrameCounter().ifPresent(f -> {
			f.limit(60);
		});
		while (!window.isClosed()) {
//			window.getFrameCounter().ifPresent(c);
			try {
				Thread.sleep(5);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
}
