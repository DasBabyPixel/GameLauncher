package gamelauncher.engine.event;

/**
 * @author DasBabyPixel
 */
public class EventException extends Exception {

	/**
	 * 
	 */
	public EventException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public EventException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public EventException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public EventException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public EventException(Throwable cause) {
		super(cause);
	}
}
