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
	 * Unparks this thread
	 */
	void unpark();
	
}
