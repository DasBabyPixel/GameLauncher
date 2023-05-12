package gamelauncher.gles.gl;

import gamelauncher.engine.util.GameException;

public interface GLFactory {

    GLContext createContext() throws GameException;

}
