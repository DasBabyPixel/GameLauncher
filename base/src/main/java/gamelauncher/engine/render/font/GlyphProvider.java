package gamelauncher.engine.render.font;

import gamelauncher.engine.render.model.GlyphStaticModel;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.text.Component;

/**
 * @author DasBabyPixel
 */
public interface GlyphProvider extends GameResource {

	GlyphStaticModel loadStaticModel(Component text, int pixelHeight)
			throws GameException;

}
