package gamelauncher.gles.model;

import gamelauncher.engine.render.model.CombinedModelsModel;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;

public final class GLESCombinedModelsModel extends AbstractGameResource implements CombinedModelsModel {

    private final Model[] models;

    public GLESCombinedModelsModel(Model... models) {
        this.models = models;
    }

    @Override public void render(ShaderProgram program) throws GameException {
        throw new UnsupportedOperationException("Not correctly implemented. This should not render. Use #getModels() to query the models to be rendered");
    }

    @Override public void cleanup0() throws GameException {
        for (Model model : this.models) {
            model.cleanup();
        }
    }

    @Override public Model[] getModels() {
        return this.models;
    }

}
