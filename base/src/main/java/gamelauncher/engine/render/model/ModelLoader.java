package gamelauncher.engine.render.model;

import gamelauncher.engine.resource.Resource;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public interface ModelLoader {

    /**
     * @param resource
     * @return the model loaded by the resource
     * @throws GameException
     */
    Model loadModel(Resource resource) throws GameException;

}
