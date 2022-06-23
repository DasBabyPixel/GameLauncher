package gamelauncher.example;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.render.Window;

public class ExampleRenderer extends Renderer {

	@Override
	public void render(Window window) throws GameException {
		
	}

	public void set(GameLauncher launcher) {
		launcher.getGameRenderer().setRenderer(this);
	}
}
