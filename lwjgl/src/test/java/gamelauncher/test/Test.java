package gamelauncher.test;

import gamelauncher.engine.GameException;
import gamelauncher.lwjgl.LWJGLGameLauncher;

public class Test {

	public static void main(String[] args) throws GameException {
		LWJGLGameLauncher launcher = new LWJGLGameLauncher();
		launcher.start();
	}
}
