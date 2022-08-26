package gamelauncher.engine.launcher.gui;

import gamelauncher.engine.gui.LauncherBasedGui;
import gamelauncher.engine.render.texture.Texture;

/**
 * @author DasBabyPixel
 */
public interface TextureGui extends LauncherBasedGui {

	/**
	 * @return the texture
	 */
	Texture getTexture();
	
}
