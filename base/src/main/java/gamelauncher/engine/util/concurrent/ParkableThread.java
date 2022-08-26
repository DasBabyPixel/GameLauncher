package gamelauncher.engine.util.concurrent;

/**
 * @author DasBabyPixel
 */
public interface ParkableThread {

	/**
	 * Parks this thread
	 */
	void park();

	/**
	 * Parks this thread for a specified amount of nanoseconds
	 * 
	 * @param nanos
	 */
	void park(long nanos);

	/**
	 * Unparks this thread
	 */
	void unpark();

}
