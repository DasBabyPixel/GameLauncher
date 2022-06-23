package gamelauncher.lwjgl.render.model;

import gamelauncher.engine.GameException;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.lwjgl.render.Mesh;

public class MeshArrayModel implements Model {

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
