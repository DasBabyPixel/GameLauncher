package gamelauncher.lwjgl;

import gamelauncher.engine.util.GameException;
import org.fusesource.jansi.AnsiConsole;

/**
 * @author DasBabyPixel
 */
public class Start {

    public static void main(String[] args) throws GameException {
        AnsiConsole.systemInstall();
        Runtime.getRuntime().addShutdownHook(new Thread(AnsiConsole::systemUninstall));
        new LWJGLGameLauncher().start(args);
    }
}
