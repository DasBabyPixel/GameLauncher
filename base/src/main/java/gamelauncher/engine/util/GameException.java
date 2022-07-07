package gamelauncher.engine.util;

/**
 * @author DasBabyPixel
 */
public class GameException extends Exception {

	/**
	 * 
	 */
	public GameException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public GameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public GameException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public GameException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public GameException(Throwable cause) {
		super(cause);
	}
}
