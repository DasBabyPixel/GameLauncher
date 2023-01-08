package gamelauncher.engine.util;

import java.util.ArrayDeque;
import java.util.Deque;

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
	public GameException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
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

	public static class Stack {

		private final Deque<GameException> exceptions = new ArrayDeque<>();

		public void add(GameException ex) {
			exceptions.offer(ex);
		}

		public void work() throws GameException {
			if (!exceptions.isEmpty()) {
				GameException ex = exceptions.poll();
				GameException sup;
				while ((sup = exceptions.poll()) != null) {
					ex.addSuppressed(sup);
				}
				throw ex;
			}
		}
	}
}
