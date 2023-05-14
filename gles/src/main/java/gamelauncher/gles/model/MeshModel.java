package gamelauncher.gles.model;

import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.Color;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.mesh.Mesh;
import gamelauncher.gles.render.MeshRenderer;
import java8.util.concurrent.CompletableFuture;

/**
 * @author DasBabyPixel
 */
public class MeshModel extends AbstractGameResource implements Model {
    protected final Mesh mesh;

    public MeshModel(Mesh mesh) {
        this.mesh = mesh;
    }

    @Override protected CompletableFuture<Void> cleanup0() throws GameException {
        return this.mesh.cleanup();
    }

    public Mesh mesh() {
        return mesh;
    }

    @Override public void render(ShaderProgram program) throws GameException {
        int id = program.getLauncher().modelIdRegistry().id(this);
        program.uId.set(Color.fred(id), Color.fgreen(id), Color.fblue(id), Color.falpha(id));
        program.getLauncher().serviceProvider().service(MeshRenderer.class).render(program, mesh);
    }
}
