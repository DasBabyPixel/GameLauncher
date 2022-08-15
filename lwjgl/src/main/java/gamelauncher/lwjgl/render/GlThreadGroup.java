package gamelauncher.lwjgl.render;

import gamelauncher.engine.util.concurrent.AbstractExecutorThread;

/**
 * @author DasBabyPixel
 */
public class GlThreadGroup extends ThreadGroup {

	/**
	 */
	public GlThreadGroup() {
		super("GL-Threads");
	}

	/**
	 * @param glThread
	 */
	public void terminated(AbstractExecutorThread glThread) {
		// Utility for future?
	}

}
