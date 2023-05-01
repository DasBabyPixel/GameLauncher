package gamelauncher.gles.model;

import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.mesh.Mesh;
import gamelauncher.gles.states.StateRegistry;
import org.joml.Vector4f;

import java.awt.*;

/**
 * @author DasBabyPixel
 */
public class MeshModel extends AbstractGameResource implements Model {
    private final Mesh mesh;

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
        Color color = new Color(program.getLauncher().modelIdRegistry().id(this), true);
        program.uid.set(new Vector4f(color.getRed() / 255F, color.getBlue() / 255F, color.getGreen() / 255F, color.getAlpha() / 255F));
        program.uploadUniforms();
        this.mesh.render(StateRegistry.currentGl());
    }
}
