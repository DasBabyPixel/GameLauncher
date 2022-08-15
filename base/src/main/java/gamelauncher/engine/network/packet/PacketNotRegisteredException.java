package gamelauncher.engine.network.packet;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public class PacketNotRegisteredException extends GameException {

	/**
	 * 
	 */
	public PacketNotRegisteredException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public PacketNotRegisteredException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public PacketNotRegisteredException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public PacketNotRegisteredException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public PacketNotRegisteredException(Throwable cause) {
		super(cause);
	}

}
