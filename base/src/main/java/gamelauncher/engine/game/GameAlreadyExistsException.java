package gamelauncher.engine.game;

import gamelauncher.engine.GameException;

public class GameAlreadyExistsException extends GameException {

	public GameAlreadyExistsException() {
		super();
	}

	public GameAlreadyExistsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GameAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public GameAlreadyExistsException(String message) {
		super(message);
	}

	public GameAlreadyExistsException(Throwable cause) {
		super(cause);
	}
}
