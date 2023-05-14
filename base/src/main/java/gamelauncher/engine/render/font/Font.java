package gamelauncher.engine.render.font;

import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;
import java8.util.concurrent.CompletableFuture;

import java.nio.ByteBuffer;

/**
 * @author DasBabyPixel
 */
public interface Font extends GameResource {

    /**
     * @return the data of this font
     * @throws GameException an exception
     */
    ByteBuffer data() throws GameException;

    CompletableFuture<ByteBuffer> dataFuture();

}
