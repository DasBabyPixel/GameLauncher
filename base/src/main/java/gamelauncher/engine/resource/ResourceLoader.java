package gamelauncher.engine.resource;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public abstract class ResourceLoader extends AbstractGameResource {

	private static ResourceLoader instance = null;
	private final Map<Path, Resource> resources = new ConcurrentHashMap<>();
	private final Lock lock = new ReentrantLock();

	/**
	 * Sets this as the {@link ResourceLoader}
	 */
	public final void set() {
		instance = this;
	}

	/**
	 * @param path
	 * @return if this {@link ResourceLoader} has the given {@link Path}
	 * @throws GameException
	 */
	public final boolean hasResource(Path path) throws GameException {
		if (isResourceLoaded(path)) {
			return true;
		}
		return canLoadResource(path);
	}

	protected abstract boolean canLoadResource(Path path) throws GameException;

	protected abstract Resource loadResource(Path path) throws GameException;

	/**
	 * @param path
	 * @return if this {@link ResourceLoader} has loaded a {@link Resource} for the given {@link Path}
	 */
	public final boolean isResourceLoaded(Path path) {
		return resources.containsKey(path);
	}

	/**
	 * Loads a {@link Resource} (if neccessary) by the given {@link Path}
	 * 
	 * @param path
	 * @return the {@link Resource}
	 * @throws GameException
	 */
	public final Resource getResource(Path path) throws GameException {
		path = path.toAbsolutePath();
		if (isResourceLoaded(path)) {
			lock.lock();
			Resource resource = resources.get(path);
			lock.unlock();
			return resource;
		}
		lock.lock();
		Resource resource = loadResource(path);
		resources.put(path, resource);
		lock.unlock();
		return resource;
	}
	
	@Override
	protected void cleanup0() throws GameException {
		for(Path path : resources.keySet()) {
			resources.remove(path).cleanup();
		}
	}

	/**
	 * @return the instance
	 */
	public static ResourceLoader getInstance() {
		return instance;
	}
}
