package gamelauncher.engine.render.font;

import java.nio.ByteBuffer;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameResource;

/**
 * @author DasBabyPixel
 */
public interface Font extends GameResource {

	/**
	 * @return the data of this font
	 * @throws GameException
	 */
	ByteBuffer data() throws GameException;

}
