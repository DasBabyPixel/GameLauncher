package gamelauncher.engine.game;

import gamelauncher.engine.GameException;

public class WrongGameException extends GameException {

	public WrongGameException() {
		super();
	}

	public WrongGameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public WrongGameException(String message, Throwable cause) {
		super(message, cause);
	}

	public WrongGameException(String message) {
		super(message);
	}

	public WrongGameException(Throwable cause) {
		super(cause);
	}
}
