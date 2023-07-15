package gamelauncher.gles.gl;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.util.GameException;

public interface GLFactory {
    @Api GLContext createContext() throws GameException;
}
