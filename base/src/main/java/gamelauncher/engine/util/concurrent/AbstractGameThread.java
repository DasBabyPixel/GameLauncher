package gamelauncher.engine.util.concurrent;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;
import gamelauncher.engine.util.function.GameSupplier;
import java8.util.concurrent.CompletableFuture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DasBabyPixel
 */
public abstract class AbstractGameThread extends java.lang.Thread implements GameResource, Thread {

    protected final CompletableFuture<Void> cleanupFuture = new CompletableFuture<>();
    private final GameLauncher launcher;
    private final Map<Key, Object> map = new ConcurrentHashMap<>();

    @Api public AbstractGameThread(GameLauncher launcher) {
        super();
        this.launcher = launcher;
        startTracking();
    }

    @Api public AbstractGameThread(GameLauncher launcher, Runnable target, String name) {
        super(target, name);
        this.launcher = launcher;
        startTracking();
    }

    @Api public AbstractGameThread(GameLauncher launcher, Runnable target) {
        super(target);
        this.launcher = launcher;
        startTracking();
    }

    @Api public AbstractGameThread(GameLauncher launcher, String name) {
        super(name);
        this.launcher = launcher;
        startTracking();
    }

    @Api public AbstractGameThread(GameLauncher launcher, ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
        this.launcher = launcher;
        startTracking();
    }

    @Api public AbstractGameThread(GameLauncher launcher, ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
        this.launcher = launcher;
        startTracking();
    }

    @Api public AbstractGameThread(GameLauncher launcher, ThreadGroup group, Runnable target) {
        super(group, target);
        this.launcher = launcher;
        startTracking();
    }

    @Api public AbstractGameThread(GameLauncher launcher, ThreadGroup group, String name) {
        super(group, name);
        this.launcher = launcher;
        startTracking();
    }

    @SuppressWarnings("unchecked") @Override public <T> T storedValue(Key key) {
        return (T) map.get(key);
    }

    @SuppressWarnings("unchecked") @Override public <T> T storedValue(Key key, GameSupplier<T> defaultSupplier) {
        return (T) map.computeIfAbsent(key, key1 -> {
            try {
                return defaultSupplier.get();
            } catch (GameException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override public void storeValue(Key key, Object value) {
        map.put(key, value);
    }

    @Override public final CompletableFuture<Void> cleanup() throws GameException {
        CompletableFuture<Void> f = this.cleanup0();
        stopTracking();
        if (f == null) {
            cleanupFuture.complete(null);
        } else {
            f.thenRun(() -> cleanupFuture.complete(null));
            f.exceptionally(throwable -> {
                cleanupFuture.completeExceptionally(throwable);
                return null;
            });
        }
        return cleanupFuture;
    }

    @Override public final CompletableFuture<Void> cleanupFuture() {
        return this.cleanupFuture;
    }

    @Override public final boolean cleanedUp() {
        return cleanupFuture.isDone();
    }

    public GameLauncher launcher() {
        return launcher;
    }

    protected abstract CompletableFuture<Void> cleanup0() throws GameException;
}
