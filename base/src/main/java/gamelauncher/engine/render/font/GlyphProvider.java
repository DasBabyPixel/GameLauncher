package gamelauncher.engine.render.font;

import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameResource;

/**
 * @author DasBabyPixel
 */
public interface GlyphProvider extends GameResource {

	/**
	 * @param font
	 * @param text
	 * @param pixelHeight
	 * @return the model for the text
	 * @throws GameException
	 */
	Model loadStaticModel(Font font, String text, int pixelHeight) throws GameException;

}
