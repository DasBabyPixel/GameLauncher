package gamelauncher.lwjgl;

import gamelauncher.engine.util.GameException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * @author DasBabyPixel
 */
public class Start {
    public static void main(String[] args) throws GameException, URISyntaxException, MalformedURLException {
        new LWJGLGameLauncher().start(args);
    }
}
