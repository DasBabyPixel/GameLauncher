package gamelauncher.engine.resource;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import gamelauncher.engine.util.Arrays;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 *
 */
public abstract class AbstractGameResource implements GameResource {

	private volatile boolean cleanedUp = false;

	private final StackTraceElement[] stack;

	private final String exName;

	private static final Collection<GameResource> resources = ConcurrentHashMap.newKeySet();

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
	 * Cleanes up this {@link AbstractGameResource resource}
	 * 
	 * @throws GameException
	 */
	@Override
	public final void cleanup() throws GameException {
		if (!isCleanedUp()) {
			setCleanedUp();
			cleanup0();
			logCleanup(this);
		} else {
			new GameException("Multiple cleanups").printStackTrace();
		}
	}

	@Override
	public boolean isCleanedUp() {
		return cleanedUp;
	}

	protected void setCleanedUp() {
		cleanedUp = true;
	}

	protected abstract void cleanup0() throws GameException;

	/**
	 * @param resource
	 */
	public static void create(GameResource resource) {
		resources.add(resource);
	}

	/**
	 * @param resource
	 */
	public static void logCleanup(GameResource resource) {
		resources.remove(resource);
	}

	/**
	 * Called on exit
	 */
	public static void exit() {
		for (GameResource resource : resources) {
			System.out.println("Memory Leak: " + resource);
			if (resource instanceof AbstractGameResource aresource) {
				Exception ex = new Exception("Stack: " + aresource.exName);
				ex.setStackTrace(aresource.stack);
				ex.printStackTrace();
			}
		}
	}

}
