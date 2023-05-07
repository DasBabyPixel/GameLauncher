package gamelauncher.engine.render.model;

/**
 * A model to combine multiple models in one
 *
 * @author DasBabyPixel
 */
public interface CombinedModelsModel extends Model {

    /**
     * @return the models combined by this model
     */
    Model[] getModels();
}
