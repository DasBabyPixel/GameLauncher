package gamelauncher.engine.game;

import java.util.Objects;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.plugin.Plugin;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;

/**
 * @author DasBabyPixel
 *
 */
public abstract class Game implements Comparable<Game> {

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
		this.launcher = key.getPlugin().getLauncher();
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
	public Key getKey() {
		return this.key;
	}

	/**
	 * @return the {@link GameLauncher}
	 */
	public GameLauncher getLauncher() {
		return this.launcher;
	}

	protected abstract void launch0(Framebuffer framebuffer) throws GameException;

	/**
	 * Launches a {@link Game} on a {@link Framebuffer}
	 * 
	 * @param framebuffer
	 * @throws GameException
	 */
	public final void launch(Framebuffer framebuffer) throws GameException {
		if (this.launcher.getCurrentGame() != null) {
			this.launcher.getCurrentGame().close();
		}
		this.launcher.setCurrentGame(this);
		this.launch0(framebuffer);
	}

	protected abstract void close0() throws GameException;

	/**
	 * @throws GameException
	 * @throws WrongGameException
	 */
	public final void close() throws GameException {
		if (this.launcher.getCurrentGame() != this) {
			throw new WrongGameException();
		}
		this.close0();
		this.launcher.setCurrentGame(null);
	}

}
