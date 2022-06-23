package gamelauncher.engine.render.model;

import gamelauncher.engine.GameException;
import gamelauncher.engine.resource.Resource;

public interface ModelLoader {

	Model loadModel(Resource resource) throws GameException;
	
}
