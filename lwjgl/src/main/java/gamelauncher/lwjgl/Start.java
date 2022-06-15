package gamelauncher.lwjgl;

import gamelauncher.engine.GameException;

public class Start {
	
	public static void main(String[] args) throws GameException {
		new LWJGLGameLauncher().start();
	}
}
