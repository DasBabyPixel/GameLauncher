package gamelauncher.engine.util.function;

import gamelauncher.engine.util.GameException;
import java8.util.concurrent.CompletableFuture;

/**
 * @param <T>
 * @author DasBabyPixel
 */
public interface GameCallable<T> {

    T call() throws GameException;

    /**
     * Creates a new futuristic runnable from this callable
     *
     * @return a new futuristic runnable
     */
    default FuturisticGameRunnable<T> toRunnable() {
        return new FuturisticGameRunnable<>(this);
    }

    /**
     * @param <T>
     * @author DasBabyPixel
     */
    class FuturisticGameRunnable<T> implements GameRunnable {

        private final CompletableFuture<T> future = new CompletableFuture<>();
        private final GameCallable<T> callable;

        private FuturisticGameRunnable(GameCallable<T> callable) {
            this.callable = callable;
        }

        @Override public void run() throws GameException {
            try {
                T t = callable.call();
                future.complete(t);
            } catch (GameException ex) {
                future.completeExceptionally(ex);
                throw ex;
            }
        }

        /**
         * @return the future for when this has been run
         */
        public CompletableFuture<T> getFuture() {
            return future;
        }
    }
}
