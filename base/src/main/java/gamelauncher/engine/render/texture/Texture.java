package gamelauncher.engine.render.texture;

import java.awt.image.BufferedImage;

import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.function.GameResource;

/**
 * @author DasBabyPixel
 */
public interface Texture extends GameResource {

	/**
	 * @return a {@link BufferedImage} for this {@link Texture}
	 */
	BufferedImage getBufferedImage();
	
	/**
	 * @param width
	 * @param height
	 */
	void allocate(int width, int height);
	
	/**
	 * @param image
	 */
	void upload(BufferedImage image);
	
	/**
	 * @param stream
	 */
	void upload(ResourceStream stream);
	
}
