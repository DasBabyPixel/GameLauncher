package gamelauncher.engine.resource;

import gamelauncher.engine.GameException;

public interface Resource {

	ResourceStream newResourceStream() throws GameException;

}
