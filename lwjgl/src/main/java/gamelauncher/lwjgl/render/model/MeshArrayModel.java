package gamelauncher.lwjgl.render.model;

import gamelauncher.engine.GameException;
import gamelauncher.lwjgl.render.Mesh;
import gamelauncher.lwjgl.render.shader.ShaderProgram;

public class MeshArrayModel implements MeshLikeModel {

	public final Mesh[] meshes;

	public MeshArrayModel(Mesh[] meshes) {
		this.meshes = meshes;
	}

	@Override
	public void cleanup() throws GameException {
		for (Mesh mesh : meshes) {
			mesh.cleanup();
		}
	}

	@Override
	public void render(ShaderProgram program) throws GameException {
		program.uploadUniforms();
		for (Mesh mesh : meshes) {
			if (mesh.getMaterial() != null) {
				program.umaterial.set(mesh.getMaterial());
			}
			program.umaterial.upload();
			program.uapplyLighting.set(mesh.applyLighting() ? 1 : 0).upload();
			mesh.render();
		}
	}
}
