package gamelauncher.engine.render.texture;

import java.util.concurrent.CompletableFuture;

import gamelauncher.engine.resource.GameResource;

/**
 * @author DasBabyPixel
 */
public interface TextureManager extends GameResource {

	/**
	 * @return a new {@link Texture}
	 */
	CompletableFuture<? extends Texture> createTexture();
	
}
