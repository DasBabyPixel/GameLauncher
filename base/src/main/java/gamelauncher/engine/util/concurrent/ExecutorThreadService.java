package gamelauncher.engine.util.concurrent;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.util.function.GameRunnable;
import java8.util.concurrent.CompletableFuture;

import java.util.Collection;
import java.util.concurrent.Executor;

/**
 * @author DasBabyPixel
 */
@Api
public interface ExecutorThreadService extends ExecutorThread {

    /**
     * @return the exitfuture
     */
    @Api
    CompletableFuture<Void> exit();

    /**
     * @return the cancelled runnables
     */
    @Api
    Collection<GameRunnable> exitNow();

    @Api
    Executor executor();

    /**
     * @return the exitfuture
     */
    @Api
    CompletableFuture<Void> exitFuture();

}
