package gamelauncher.engine.render.font;

import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public interface FontFactory {

	/**
	 * This method will close the given ResourceStream eventually
	 * 
	 * @param stream
	 * @return a font created with the given {@link ResourceStream}
	 * @throws GameException
	 */
	Font createFont(ResourceStream stream) throws GameException;

}
