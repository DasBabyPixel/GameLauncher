package gamelauncher.engine.render.shader;

import gamelauncher.engine.util.GameException;

import java.nio.file.Path;

/**
 * @author DasBabyPixel
 */
public interface ShaderLoader {

    /**
     * @param path
     * @return the {@link ShaderProgram} loaded from the path
     * @throws GameException
     */
    ShaderProgram loadShader(Path path) throws GameException;

}
