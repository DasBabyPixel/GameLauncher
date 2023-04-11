package gamelauncher.engine.util.concurrent;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;
import gamelauncher.engine.util.function.GameSupplier;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DasBabyPixel
 */
public abstract class AbstractGameThread extends Thread implements GameResource {

    protected volatile boolean cleanedUp = false;

    private final GameLauncher launcher;
    protected final CompletableFuture<Void> cleanupFuture = new CompletableFuture<>();
    private final Map<Key, Object> map = new ConcurrentHashMap<>();

    /**
     *
     */
    public AbstractGameThread(GameLauncher launcher) {
        super();
        this.launcher = launcher;
        AbstractGameResource.create(this);
    }

    public AbstractGameThread(GameLauncher launcher, Runnable target, String name) {
        super(target, name);
        this.launcher = launcher;
        AbstractGameResource.create(this);
    }

    public AbstractGameThread(GameLauncher launcher, Runnable target) {
        super(target);
        this.launcher = launcher;
        AbstractGameResource.create(this);
    }

    public AbstractGameThread(GameLauncher launcher, String name) {
        super(name);
        this.launcher = launcher;
        AbstractGameResource.create(this);
    }

    public AbstractGameThread(GameLauncher launcher, ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
        this.launcher = launcher;
        AbstractGameResource.create(this);
    }

    public AbstractGameThread(GameLauncher launcher, ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
        this.launcher = launcher;
        AbstractGameResource.create(this);
    }

    public AbstractGameThread(GameLauncher launcher, ThreadGroup group, Runnable target) {
        super(group, target);
        this.launcher = launcher;
        AbstractGameResource.create(this);
    }

    public AbstractGameThread(GameLauncher launcher, ThreadGroup group, String name) {
        super(group, name);
        this.launcher = launcher;
        AbstractGameResource.create(this);
    }

    @Override
    public <T> T storedValue(Key key) {
        //noinspection unchecked
        return (T) map.get(key);
    }

    @Override
    public <T> T storedValue(Key key, GameSupplier<T> defaultSupplier) {
        //noinspection unchecked
        return (T) map.computeIfAbsent(key, key1 -> {
            try {
                return defaultSupplier.get();
            } catch (GameException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void storeValue(Key key, Object value) {
        map.put(key, value);
    }

    @Override
    public final void cleanup() throws GameException {
        if (!this.cleanedUp) {
            this.cleanup0();
            this.cleanupFuture.complete(null);
            this.cleanedUp = true;
            AbstractGameResource.logCleanup(this);
        }
    }

    @Override
    public CompletableFuture<Void> cleanupFuture() {
        return this.cleanupFuture;
    }

    @Override
    public boolean cleanedUp() {
        return this.cleanedUp;
    }

    protected abstract void cleanup0() throws GameException;

    public GameLauncher launcher() {
        return launcher;
    }
}
