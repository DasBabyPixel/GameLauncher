package gamelauncher.example;

import gamelauncher.engine.plugin.Plugin;
import gamelauncher.engine.plugin.Plugin.GamePlugin;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;

@GamePlugin
public class Example extends Plugin {

	public Example() {
		super("example");
	}

	@Override
	public void onEnable() throws GameException {
		this.getLauncher().getGameRegistry().register(new ExampleGame(this));
	}

	@Override
	public void onDisable() {
		this.getLauncher().getGameRegistry().unregister(new Key(this, "example"));
	}

}
