package gamelauncher.engine.render;

import gamelauncher.engine.util.concurrent.ExecutorThread;

/**
 * @author DasBabyPixel
 */
public interface RenderThread extends ExecutorThread {

	/**
	 * @return the frame of this {@link RenderThread}
	 */
	Frame getFrame();
	
	/**
	 * @return the name of this {@link Thread}
	 */
	String getName();
	
}
