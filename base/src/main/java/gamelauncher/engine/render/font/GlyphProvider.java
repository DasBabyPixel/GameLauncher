package gamelauncher.engine.render.font;

import gamelauncher.engine.GameException;
import gamelauncher.engine.render.Model;
import gamelauncher.engine.util.GameResource;

public interface GlyphProvider extends GameResource {

	Model loadStaticModel(Font font, String text, int pixelHeight) throws GameException;

}
