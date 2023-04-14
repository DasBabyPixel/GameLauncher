package gamelauncher.gles.model;

import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.gl.GLES31;
import gamelauncher.gles.mesh.Mesh;
import gamelauncher.gles.states.StateRegistry;

/**
 * @author DasBabyPixel
 */
public class MeshArrayModel extends AbstractGameResource implements Model {

    private final Mesh[] meshes;

    public MeshArrayModel(Mesh[] meshes) {
        this.meshes = meshes;
    }

    @Override
    public void cleanup0() throws GameException {
        for (Mesh mesh : this.meshes) {
            mesh.cleanup();
        }
    }

    @Override
    public void render(ShaderProgram program) throws GameException {
        GLES31 gl = StateRegistry.currentGl();
        for (Mesh mesh : this.meshes) {
            if (mesh.material() != null) {
                program.umaterial.set(mesh.material());
            }
//			program.umaterial.upload();
            program.uapplyLighting.set(mesh.applyLighting() ? 1 : 0);
            program.uploadUniforms();
            mesh.render(gl);
        }
    }
}
