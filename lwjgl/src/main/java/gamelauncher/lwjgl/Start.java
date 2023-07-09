package gamelauncher.lwjgl;

import gamelauncher.engine.util.GameException;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author DasBabyPixel
 */
public class Start {
    public static void main(String[] args) throws GameException, URISyntaxException, IOException {
        new LWJGLGameLauncher().start(args);
    }
}
