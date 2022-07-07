package gamelauncher.engine.game;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 *
 */
public class WrongGameException extends GameException {

	/**
	 * 
	 */
	public WrongGameException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public WrongGameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public WrongGameException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public WrongGameException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public WrongGameException(Throwable cause) {
		super(cause);
	}
}
