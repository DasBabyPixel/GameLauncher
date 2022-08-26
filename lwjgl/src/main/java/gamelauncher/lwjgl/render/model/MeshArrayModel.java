package gamelauncher.lwjgl.render.model;

import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.render.mesh.Mesh;

/**
 * @author DasBabyPixel
 *
 */
public class MeshArrayModel extends AbstractGameResource implements Model {

	private final Mesh[] meshes;

	/**
	 * @param meshes
	 */
	public MeshArrayModel(Mesh[] meshes) {
		this.meshes = meshes;
	}

	@Override
	public void cleanup0() throws GameException {
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
