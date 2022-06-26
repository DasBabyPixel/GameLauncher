package gamelauncher.engine.resource;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public interface Resource {

	/**
	 * Creates a new {@link ResourceStream} for this {@link Resource}
	 * 
	 * @return the {@link ResourceStream}
	 * @throws GameException
	 */
	ResourceStream newResourceStream() throws GameException;

}
