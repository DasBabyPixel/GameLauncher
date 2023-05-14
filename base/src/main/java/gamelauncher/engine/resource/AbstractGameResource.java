package gamelauncher.engine.resource;

import gamelauncher.engine.util.Arrays;
import gamelauncher.engine.util.Debug;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.logging.Logger;
import java8.util.concurrent.CompletableFuture;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author DasBabyPixel
 */
public abstract class AbstractGameResource extends StorageResource {

    private static final Logger logger = Logger.logger();
    private final CompletableFuture<Void> cleanupFuture = new CompletableFuture<>();
    private final AtomicBoolean calledCleanup = new AtomicBoolean(false);
    StackTraceElement[] creationStack;
    String creationThreadName;
    StackTraceElement[] cleanupStack;
    String cleanupThreadName;

    public AbstractGameResource() {
        if (Debug.trackResources) {
            StackTraceElement[] es = new Exception().getStackTrace();
            this.creationThreadName = Thread.currentThread().getName();
            this.creationStack = Arrays.copyOfRange(es, 1, es.length);
        }
        if (autoTrack()) startTracking();
    }

    /**
     * This will only be called once in the constructor.
     * This should be overridden.
     *
     * @return whether this resource should be tracked as soon as it is created.
     */
    protected boolean autoTrack() {
        return true;
    }

    /**
     * Cleanes up this {@link AbstractGameResource resource}
     *
     * @throws GameException an exception
     */
    @Override public final CompletableFuture<Void> cleanup() throws GameException {
        if (calledCleanup.compareAndSet(false, true)) {
            if (Debug.trackResources) {
                cleanupStack = new Exception().getStackTrace();
                cleanupStack = Arrays.copyOfRange(cleanupStack, 1, cleanupStack.length);
                cleanupThreadName = Thread.currentThread().getName();
            }
            CompletableFuture<Void> f = this.cleanup0();
            if (f == null) {
                cleanupFuture.complete(null);
                stopTracking();
            } else {
                f.thenRun(() -> {
                    cleanupFuture.complete(null);
                    stopTracking();
                }).exceptionally(t -> {
                    cleanupFuture.completeExceptionally(t);
                    stopTracking();
                    return null;
                });
            }
        } else {
            GameException ex = new GameException("Multiple cleanups");
            if (Debug.trackResources) {
                GameException ex2 = new GameException("CreationStack: " + creationThreadName);
                GameException ex3 = new GameException("CleanupStack: " + cleanupThreadName);
                ex2.setStackTrace(creationStack);
                ex3.setStackTrace(cleanupStack);
                ex.addSuppressed(ex2);
                ex.addSuppressed(ex3);
            }
            logger.error(ex);
        }
        return cleanupFuture;
    }

    @Override public boolean cleanedUp() {
        return cleanupFuture.isDone();
    }

    @Override public CompletableFuture<Void> cleanupFuture() {
        return this.cleanupFuture;
    }

    protected abstract CompletableFuture<Void> cleanup0() throws GameException;

}
