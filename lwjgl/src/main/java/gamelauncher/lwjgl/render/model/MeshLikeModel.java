package gamelauncher.lwjgl.render.model;

import gamelauncher.engine.GameException;
import gamelauncher.engine.render.Model;
import gamelauncher.lwjgl.render.ShaderProgram;

public interface MeshLikeModel extends Model {

	void render(ShaderProgram program) throws GameException;
	
}
