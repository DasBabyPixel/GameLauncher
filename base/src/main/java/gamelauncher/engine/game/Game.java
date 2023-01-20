package gamelauncher.engine.game;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.plugin.Plugin;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;

import java.util.Objects;

/**
 * @author DasBabyPixel
 */
public abstract class Game implements Comparable<Game> {

	private final GameLauncher launcher;

	private final Key key;

	public Game(Plugin plugin, String name) {
		this(new Key(plugin, name));
	}

	public Game(Key key) {
		this.key = key;
		this.launcher = key.plugin().launcher();
	}

	@Override
	public int compareTo(Game o) {
		return -Integer.compare(this.hashCode(), o.hashCode());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.key);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		Game other = (Game) obj;
		return Objects.equals(this.key, other.key);
	}

	/**
	 * @return the game {@link Key}
	 */
	public Key key() {
		return this.key;
	}

	/**
	 * @return the {@link GameLauncher}
	 */
	public GameLauncher launcher() {
		return this.launcher;
	}

	protected abstract void launch0(Framebuffer framebuffer) throws GameException;

	/**
	 * Launches a {@link Game} on a {@link Framebuffer}
	 *
	 * @param framebuffer a framebuffer
	 *
	 * @throws GameException an exception
	 */
	public final void launch(Framebuffer framebuffer) throws GameException {
		if (this.launcher.currentGame() != null) {
			this.launcher.currentGame().close();
		}
		this.launcher.currentGame(this);
		this.launch0(framebuffer);
	}

	protected abstract void close0() throws GameException;

	/**
	 * @throws GameException an exception
	 * @throws WrongGameException an exception
	 */
	public final void close() throws GameException {
		if (this.launcher.currentGame() != this) {
			throw new WrongGameException();
		}
		this.close0();
		this.launcher.currentGame(null);
	}

}
