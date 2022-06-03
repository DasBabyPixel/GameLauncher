package gamelauncher.lwjgl.render.model;

import gamelauncher.engine.GameException;
import gamelauncher.lwjgl.render.Mesh;
import gamelauncher.lwjgl.render.ShaderProgram;

public class MeshArrayModel implements MeshLikeModel {

	public final Mesh[] meshes;

	public MeshArrayModel(Mesh[] meshes) {
		this.meshes = meshes;
	}

	@Override
	public void cleanup() {
		for (Mesh mesh : meshes) {
			mesh.cleanup();
		}
	}

	@Override
	public void render(ShaderProgram program) throws GameException {
		for (Mesh mesh : meshes) {
			if (mesh.getMaterial() != null)
				program.setUniform("material", mesh.getMaterial());
			mesh.render();
		}
	}
}
