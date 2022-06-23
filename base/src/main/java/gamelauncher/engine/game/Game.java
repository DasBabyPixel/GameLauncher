package gamelauncher.engine.game;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.plugin.Plugin;
import gamelauncher.engine.util.Key;

public abstract class Game {

	private final GameLauncher launcher;
	private final Key key;

	public Game(Plugin plugin, String name) {
		this(new Key(plugin, name));
	}

	public Game(Key key) {
		this.key = key;
		this.launcher = key.plugin.getLauncher();
	}

	public Key getKey() {
		return key;
	}

	protected abstract void launch0();

	public final void launch() throws WrongGameException {
		if (launcher.getCurrentGame() != null) {
			launcher.getCurrentGame().close();
		}
		launcher.setCurrentGame(this);
		launch0();
	}

	protected abstract void close0();

	public final void close() throws WrongGameException {
		if (launcher.getCurrentGame() != this) {
			throw new WrongGameException();
		}
		close0();
		launcher.setCurrentGame(null);
	}
}
