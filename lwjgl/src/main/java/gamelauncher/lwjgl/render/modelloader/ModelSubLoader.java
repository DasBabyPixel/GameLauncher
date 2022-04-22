package gamelauncher.lwjgl.render.modelloader;

import gamelauncher.engine.GameException;
import gamelauncher.engine.resource.ResourceStream;

public interface ModelSubLoader {

	byte[] convertModel(ResourceStream in) throws GameException;

}
