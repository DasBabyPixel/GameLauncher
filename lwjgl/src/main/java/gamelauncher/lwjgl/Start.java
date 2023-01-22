package gamelauncher.lwjgl;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public class Start {

	public static void main(String[] args) throws GameException {
		System.out.println("Starting LWJGL");
		new LWJGLGameLauncher().start(args);
	}
}
