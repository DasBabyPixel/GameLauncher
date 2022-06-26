package gamelauncher.engine.game;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 *
 */
public class GameAlreadyExistsException extends GameException {

	/**
	 * 
	 */
	public GameAlreadyExistsException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public GameAlreadyExistsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public GameAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public GameAlreadyExistsException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public GameAlreadyExistsException(Throwable cause) {
		super(cause);
	}
}
