package gamelauncher.engine.util.profiler;

/**
 * @author DasBabyPixel
 */
public interface SectionHandler {

	/**
	 * @param type
	 * @param section
	 */
	default void handleBegin(String type, String section) {
	}

	/**
	 * Executes all checks
	 * 
	 * @param type
	 * @param section
	 */
	default void check(String type, String section) {
	}

	/**
	 * @param type
	 * @param section
	 * @param tookNanos
	 */
	default void handleEnd(String type, String section, long tookNanos) {
	}

}
