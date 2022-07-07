package gamelauncher.engine.event;

/**
 * @author DasBabyPixel
 *
 */
public interface Cancellable {

	/**
	 * @return if the event is cancelled
	 */
	boolean isCancelled();

	/**
	 * Sets if the event is cancelled
	 * 
	 * @param cancel
	 */
	void setCancelled(boolean cancel);

}
