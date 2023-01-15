package gamelauncher.engine.game;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;

/**
 * @author DasBabyPixel
 */
public class GameRegistry {

	private final Map<Key, Game> games = new ConcurrentHashMap<>();

	/**
	 * @param key
	 *
	 * @return the game or null
	 */
	public Game get(Key key) {
		return games.get(key);
	}

	/**
	 * @param game
	 *
	 * @throws GameException
	 */
	public void register(Game game) throws GameException {
		if (games.putIfAbsent(game.key(), game) != null) {
			throw new GameAlreadyExistsException();
		}
	}

	/**
	 * @param key
	 *
	 * @return the unregistered {@link Game} or null if no {@link Game} was found
	 */
	public Game unregister(Key key) {
		return games.remove(key);
	}

	/**
	 * @return the games
	 */
	public Collection<Game> games() {
		return Collections.unmodifiableCollection(games.values());
	}

}
