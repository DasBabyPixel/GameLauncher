package gamelauncher.engine.game;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DasBabyPixel
 */
public class GameRegistry {

    private final Map<Key, Game> games = new ConcurrentHashMap<>();

    /**
     * @return the game or null
     */
    public Game get(Key key) {
        return games.get(key);
    }

    public void register(Game game) throws GameException {
        if (games.putIfAbsent(game.key(), game) != null) {
            throw new GameAlreadyExistsException();
        }
    }

    /**
     * @return the unregistered {@link Game} or null if no {@link Game} was found
     */
    public Game unregister(Key key) {
        return games.remove(key);
    }

    /**
     * @return the games
     */
    @Api public Collection<Game> games() {
        return Collections.unmodifiableCollection(games.values());
    }

}
