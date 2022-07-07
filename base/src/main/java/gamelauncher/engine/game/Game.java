package gamelauncher.engine.game;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.plugin.Plugin;
import gamelauncher.engine.util.Key;

/**
 * @author DasBabyPixel
 *
 */
public abstract class Game {

	private final GameLauncher launcher;
	private final Key key;

	/**
	 * @param plugin
	 * @param name
	 */
	public Game(Plugin plugin, String name) {
		this(new Key(plugin, name));
	}

	/**
	 * @param key
	 */
	public Game(Key key) {
		this.key = key;
		this.launcher = key.plugin.getLauncher();
	}

	/**
	 * @return the game {@link Key}
	 */
	public Key getKey() {
		return key;
	}

	protected abstract void launch0();

	/**
	 * @throws WrongGameException
	 */
	public final void launch() throws WrongGameException {
		if (launcher.getCurrentGame() != null) {
			launcher.getCurrentGame().close();
		}
		launcher.setCurrentGame(this);
		launch0();
	}

	protected abstract void close0();

	/**
	 * @throws WrongGameException
	 */
	public final void close() throws WrongGameException {
		if (launcher.getCurrentGame() != this) {
			throw new WrongGameException();
		}
		close0();
		launcher.setCurrentGame(null);
	}
}
