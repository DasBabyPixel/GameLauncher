package gamelauncher.engine.game;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import gamelauncher.engine.GameException;
import gamelauncher.engine.util.Key;

public class GameRegistry {

	public final Map<Key, Game> games = new ConcurrentHashMap<>();

	public Game get(Key key) {
		return games.get(key);
	}

	public void register(Game game) throws GameException {
		if (games.put(game.getKey(), game) == null) {
			throw new GameAlreadyExistsException();
		}
	}
}
