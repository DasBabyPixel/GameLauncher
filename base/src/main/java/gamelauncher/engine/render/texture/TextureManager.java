package gamelauncher.engine.render.texture;

import java.util.concurrent.CompletableFuture;

import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public interface TextureManager extends GameResource {

	/**
	 * @return a new {@link Texture}
	 * @throws GameException 
	 */
	CompletableFuture<? extends Texture> createTexture() throws GameException;
	
}
