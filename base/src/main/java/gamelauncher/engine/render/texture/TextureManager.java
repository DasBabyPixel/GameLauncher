package gamelauncher.engine.render.texture;

import gamelauncher.engine.util.function.GameResource;

/**
 * @author DasBabyPixel
 */
public interface TextureManager extends GameResource {

	/**
	 * @return a new {@link Texture}
	 */
	Texture createTexture();
	
}
