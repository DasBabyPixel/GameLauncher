package gamelauncher.engine.render.font;

import java.nio.ByteBuffer;

import gamelauncher.engine.GameException;
import gamelauncher.engine.util.GameResource;

public interface Font extends GameResource {

	ByteBuffer data() throws GameException;

}
