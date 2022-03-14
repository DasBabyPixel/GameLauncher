package game;

import static org.lwjgl.opengl.GL11.*;

import java.util.function.*;

import game.render.*;
import game.resource.*;

public class Game {

	public Window window;
	public final GameRenderer gameRenderer = new GameRenderer();

	public void start() {
		if (window != null) {
			return;
		}
		
		new EmbedResourceLoader().set();
		
		window = new Window(400, 400, "Game");
		gameRenderer.setRenderer(null);
		window.frameRenderer.set(gameRenderer);
		window.renderLater(() -> {
			glClearColor(.2F, .2F, .2F, .8F);
		});
		window.setRenderMode(RenderMode.ON_UPDATE);
		
		window.createWindow();
		window.startRendering();
		window.show();

		Consumer<FrameCounter> c = new Consumer<FrameCounter>() {
			int fps = 0;

			@Override
			public void accept(FrameCounter t) {
				if (fps != t.getFps()) {
					fps = t.getFps();
					System.out.println(fps);
				}
			}
		};
		while (!window.isClosed()) {
			window.getFrameCounter().ifPresent(c);
		}
	}
}
