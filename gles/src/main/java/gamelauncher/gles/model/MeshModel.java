package gamelauncher.gles.model;

import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.mesh.Mesh;
import gamelauncher.gles.render.MeshRenderer;
import org.joml.Vector4f;

import java.awt.*;

/**
 * @author DasBabyPixel
 */
public class MeshModel extends AbstractGameResource implements Model {
    protected final Mesh mesh;

    public MeshModel(Mesh mesh) {
        this.mesh = mesh;
    }

    @Override protected void cleanup0() throws GameException {
        this.mesh.cleanup();
    }

    public Mesh mesh() {
        return mesh;
    }

    @Override public void render(ShaderProgram program) throws GameException {
        Color color = new Color(program.getLauncher().modelIdRegistry().id(this), true);
        program.uId.set(new Vector4f(color.getRed() / 255F, color.getBlue() / 255F, color.getGreen() / 255F, color.getAlpha() / 255F));
        program.getLauncher().serviceProvider().service(MeshRenderer.class).render(program, mesh);
    }
}
