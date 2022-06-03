package gamelauncher.lwjgl.render.model;

import gamelauncher.engine.GameException;
import gamelauncher.lwjgl.render.Mesh;
import gamelauncher.lwjgl.render.ShaderProgram;

public class MeshModel implements MeshLikeModel {

	private final Mesh mesh;

	public MeshModel(Mesh mesh) {
		this.mesh = mesh;
	}

	@Override
	public void cleanup() throws GameException {
		mesh.cleanup();
	}

	@Override
	public void render(ShaderProgram program) throws GameException {
		if (mesh.getMaterial() != null)
			program.setUniform("material", mesh.getMaterial());
		mesh.render();
	}
}
