package gamelauncher.lwjgl.render.model;

import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.render.mesh.Mesh;

/**
 * @author DasBabyPixel
 */
public class MeshModel extends AbstractGameResource implements Model {

	private final Mesh mesh;

	/**
	 * @param mesh
	 */
	public MeshModel(Mesh mesh) {
		this.mesh = mesh;
	}

	@Override
	protected void cleanup0() throws GameException {
		this.mesh.cleanup();
	}

	@Override
	public void render(ShaderProgram program) throws GameException {
		program.umaterial.set(this.mesh.material());
		program.uhasTexture.set(this.mesh.material().texture != null ? 1 : 0);
		program.uapplyLighting.set(this.mesh.applyLighting() ? 1 : 0);
		program.uploadUniforms();
		this.mesh.render();
	}
}
