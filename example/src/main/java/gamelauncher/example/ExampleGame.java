package gamelauncher.example;

import gamelauncher.engine.game.Game;
import gamelauncher.engine.plugin.Plugin;

public class ExampleGame extends Game {

	public ExampleGame(Plugin plugin) {
		super(plugin, "example");
	}

	@Override
	protected void launch0() {
		System.out.println("launch example game");
	}

	@Override
	protected void close0() {
		System.out.println("close example game");
	}

}
