package gamelauncher.engine.resource;

import gamelauncher.engine.util.Arrays;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;
import gamelauncher.engine.util.function.GameSupplier;
import gamelauncher.engine.util.logging.Logger;
import java8.util.concurrent.CompletableFuture;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DasBabyPixel
 */
public abstract class AbstractGameResource implements GameResource {

    private static final Collection<GameResource> resources = ConcurrentHashMap.newKeySet();
    private static Logger logger;
    private final StackTraceElement[] stack;

    private final String exName;

    private final CompletableFuture<Void> cleanupFuture = new CompletableFuture<>();
    private final Map<Key, Object> map = new ConcurrentHashMap<>();
    private volatile boolean cleanedUp = false;

    /**
     *
     */
    public AbstractGameResource() {
        StackTraceElement[] es = new Exception().getStackTrace();
        this.exName = Thread.currentThread().getName();
        this.stack = Arrays.copyOfRange(es, 1, es.length);
        create(this);
    }

    /**
     * @param resource a resource
     */
    public static void create(GameResource resource) {
        resources.add(resource);
    }

    /**
     * @param resource a resource
     */
    public static void logCleanup(GameResource resource) {
        resources.remove(resource);
    }

    private static Logger logger() {
        if (logger == null) {
            return logger = Logger.logger();
        }
        return logger;
    }

    /**
     * Called on exit
     */
    public static void exit() {
        for (GameResource resource : resources) {
            if (resource instanceof AbstractGameResource) {
                AbstractGameResource aresource = (AbstractGameResource) resource;
                Exception ex = new Exception("Stack: " + aresource.exName);
                ex.setStackTrace(aresource.stack);
                logger().errorf("Memory Leak: %s%n%s", resource, ex);
            } else {
                logger().errorf("Memory Leak: %s", resource);
            }
        }
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

    /**
     * Cleanes up this {@link AbstractGameResource resource}
     *
     * @throws GameException an exception
     */
    @Override public final void cleanup() throws GameException {
        if (!this.cleanedUp()) {
            this.setCleanedUp();
            this.cleanup0();
            if (this.cleanedUp()) {
                this.cleanupFuture.complete(null);
                AbstractGameResource.logCleanup(this);
            }
        } else {
            GameException ex = new GameException("Multiple cleanups");
            GameException ex2 = new GameException("Stack: " + exName);
            ex2.setStackTrace(stack);
            ex.addSuppressed(ex2);
            AbstractGameResource.logger().error(ex);
        }
    }

    @Override public boolean cleanedUp() {
        return this.cleanedUp;
    }

    @Override public CompletableFuture<Void> cleanupFuture() {
        return this.cleanupFuture;
    }

    protected void setCleanedUp() {
        this.cleanedUp = true;
    }

    protected abstract void cleanup0() throws GameException;

}
