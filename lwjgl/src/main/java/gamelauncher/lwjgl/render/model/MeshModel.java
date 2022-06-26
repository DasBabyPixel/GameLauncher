package gamelauncher.lwjgl.render.model;

import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.render.Mesh;

/**
 * @author DasBabyPixel
 */
public class MeshModel implements Model {

	private final Mesh mesh;

	/**
	 * @param mesh
	 */
	public MeshModel(Mesh mesh) {
		this.mesh = mesh;
	}

	@Override
	public void cleanup() throws GameException {
		mesh.cleanup();
	}

	@Override
	public void render(ShaderProgram program) throws GameException {
		if (mesh.getMaterial() != null) {
			program.umaterial.set(mesh.getMaterial());
		}
		program.uapplyLighting.set(mesh.applyLighting() ? 1 : 0);
		program.uploadUniforms();
		mesh.render();
	}
}
