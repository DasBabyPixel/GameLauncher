package gamelauncher.engine.util.concurrent;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;
import gamelauncher.engine.util.function.GameSupplier;

/**
 * @author DasBabyPixel
 */
public abstract class AbstractGameThread extends Thread implements GameResource {

    protected volatile boolean cleanedUp = false;

    protected final CompletableFuture<Void> cleanupFuture = new CompletableFuture<>();
    private final Map<Key, Object> map = new ConcurrentHashMap<>();

    /**
     *
     */
    public AbstractGameThread() {
        super();
        AbstractGameResource.create(this);
    }

    public AbstractGameThread(Runnable target, String name) {
        super(target, name);
        AbstractGameResource.create(this);
    }

    public AbstractGameThread(Runnable target) {
        super(target);
        AbstractGameResource.create(this);
    }

    public AbstractGameThread(String name) {
        super(name);
        AbstractGameResource.create(this);
    }

    public AbstractGameThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
        AbstractGameResource.create(this);
    }

    public AbstractGameThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
        AbstractGameResource.create(this);
    }

    public AbstractGameThread(ThreadGroup group, Runnable target) {
        super(group, target);
        AbstractGameResource.create(this);
    }

    public AbstractGameThread(ThreadGroup group, String name) {
        super(group, name);
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

}
