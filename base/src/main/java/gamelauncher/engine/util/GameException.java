package gamelauncher.engine.util;

/**
 * @author DasBabyPixel
 */
public class GameException extends Exception {

    public GameException() {
        super();
    }

    public GameException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameException(String message) {
        super(message);
    }

    public GameException(Throwable cause) {
        super(cause);
    }
}
