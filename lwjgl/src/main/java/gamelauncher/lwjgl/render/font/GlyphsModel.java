package gamelauncher.lwjgl.render.font;

import java.util.Arrays;

import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.util.GameException;

@SuppressWarnings("javadoc")
public class GlyphsModel implements Model {

	private final GlyphsMesh[] meshes;

	public GlyphsModel(GlyphsMesh[] meshes) {
		this.meshes = meshes;
	}

	@Override
	public void render(ShaderProgram program) throws GameException {
		for (GlyphsMesh mesh : meshes) {
			mesh.render(program);
		}
	}

	@Override
	public void cleanup() throws GameException {
		for (GlyphsMesh mesh : meshes) {
			if (mesh != null) {
				mesh.cleanup();
			}
		}
		Arrays.fill(meshes, null);
	}
}