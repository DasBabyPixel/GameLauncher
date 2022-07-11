package gamelauncher.engine.render;

import gamelauncher.engine.util.concurrent.ExecutorThread;

/**
 * @author DasBabyPixel
 */
public interface RenderThread extends ExecutorThread {

	/**
	 * @return the window of this {@link RenderThread}
	 */
	Window getWindow();
	
	/**
	 * @return the name of this {@link Thread}
	 */
	String getName();
	
}
