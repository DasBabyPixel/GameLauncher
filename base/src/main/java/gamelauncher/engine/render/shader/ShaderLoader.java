package gamelauncher.engine.render.shader;

import java.nio.file.Path;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;

public interface ShaderLoader {

	ShaderProgram loadShader(GameLauncher launcher, Path path) throws GameException;
	
}
