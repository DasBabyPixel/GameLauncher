package gamelauncher.example;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.util.GameException;

public class ExampleRenderer extends Renderer {

	@Override
	public void render(Window window) throws GameException {
		
	}

	public void set(GameLauncher launcher) {
		launcher.getGameRenderer().setRenderer(this);
	}
}
