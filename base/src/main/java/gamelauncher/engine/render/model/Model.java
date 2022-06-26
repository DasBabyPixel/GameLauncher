package gamelauncher.engine.render.model;

import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameResource;

/**
 * @author DasBabyPixel
 */
public interface Model extends GameResource {

	/**
	 * @param program
	 * @throws GameException
	 */
	void render(ShaderProgram program) throws GameException;

}
