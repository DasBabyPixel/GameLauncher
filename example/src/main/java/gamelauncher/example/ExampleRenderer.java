package gamelauncher.example;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.util.GameException;

public class ExampleRenderer extends Renderer {

	public void set(GameLauncher launcher) {
		launcher.gameRenderer().setRenderer(this);
	}

	@Override
	public void render(Framebuffer framebuffer) throws GameException {
	}
}
