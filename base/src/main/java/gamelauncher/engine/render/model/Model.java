package gamelauncher.engine.render.model;

import gamelauncher.engine.GameException;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.util.GameResource;

public interface Model extends GameResource {

	void render(ShaderProgram program) throws GameException;

}
