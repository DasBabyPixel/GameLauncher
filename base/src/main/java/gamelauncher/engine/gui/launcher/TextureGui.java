package gamelauncher.engine.gui.launcher;

import gamelauncher.engine.gui.LauncherBasedGui;
import gamelauncher.engine.render.texture.Texture;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public interface TextureGui extends LauncherBasedGui {

	/**
	 * @return the texture
	 *
	 * @throws GameException
	 */
	Texture getTexture() throws GameException;

}
