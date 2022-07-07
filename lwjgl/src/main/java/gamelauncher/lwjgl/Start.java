package gamelauncher.lwjgl;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public class Start {
	
	/**
	 * @param args
	 * @throws GameException
	 */
	public static void main(String[] args) throws GameException {
		new LWJGLGameLauncher().start(args);
	}
}
