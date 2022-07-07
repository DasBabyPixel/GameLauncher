package gamelauncher.engine.render;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.util.function.GameResource;

/**
 * @author DasBabyPixel
 */
public interface Framebuffer extends GameResource {

	/**
	 * Called before every frame
	 */
	void beginFrame();
	
	/**
	 * Called after every frame
	 */
	void endFrame();
	
	/**
	 * @return the width property
	 */
	NumberValue width();
	
	/**
	 * @return the height property
	 */
	NumberValue height();
	
}
