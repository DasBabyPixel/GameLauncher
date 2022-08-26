package gamelauncher.engine.render.font;

import gamelauncher.engine.render.model.GlyphStaticModel;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;

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
	GlyphStaticModel loadStaticModel(Font font, String text, int pixelHeight) throws GameException;

}
