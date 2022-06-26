package gamelauncher.engine.render.shader;

import java.nio.file.Path;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public interface ShaderLoader {

	/**
	 * @param launcher
	 * @param path
	 * @return the {@link ShaderProgram} loaded from the path
	 * @throws GameException
	 */
	ShaderProgram loadShader(GameLauncher launcher, Path path) throws GameException;
	
}
