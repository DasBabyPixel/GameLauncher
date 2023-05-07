package gamelauncher.engine.render.model;

import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public interface Model extends GameResource {

    void render(ShaderProgram program) throws GameException;

}
