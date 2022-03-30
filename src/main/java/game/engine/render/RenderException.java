package game.engine.render;

public class RenderException extends Exception {

	public RenderException() {
		super();
	}

	public RenderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RenderException(String message, Throwable cause) {
		super(message, cause);
	}

	public RenderException(String message) {
		super(message);
	}

	public RenderException(Throwable cause) {
		super(cause);
	}
}
