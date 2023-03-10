package gamelauncher.engine.resource;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.LauncherBasedGui;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;
import org.jetbrains.annotations.Contract;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author DasBabyPixel
 */
public abstract class ResourceLoader extends AbstractGameResource {

	private static ResourceLoader instance = null;
	private final GameLauncher launcher;
	private final Map<Path, Resource> resources = new ConcurrentHashMap<>();
	private final Lock lock = new ReentrantLock();

	public ResourceLoader(GameLauncher launcher) {
		this.launcher = launcher;
	}

	/**
	 * Sets this as the {@link ResourceLoader}
	 */
	public final void set() {
		instance = this;
	}

	/**
	 * @param path the path
	 *
	 * @return if this {@link ResourceLoader} has the given {@link Path}
	 *
	 * @throws GameException an exception
	 */
	@Contract(pure = true)
	public final boolean hasResource(Path path) throws GameException {
		if (isResourceLoaded(path)) {
			return true;
		}
		return canLoadResource(path);
	}

	@Contract(pure = true)
	protected abstract boolean canLoadResource(Path path) throws GameException;

	protected abstract Resource loadResource(Path path) throws GameException;

	/**
	 * @param path the path
	 *
	 * @return if this {@link ResourceLoader} has loaded a {@link Resource} for the given
	 * {@link Path}
	 */
	public final boolean isResourceLoaded(Path path) {
		return resources.containsKey(path);
	}

	/**
	 * Loads a {@link Resource} (if neccessary) by the given {@link Path}
	 *
	 * @param path the path
	 *
	 * @return the {@link Resource}
	 *
	 * @throws GameException an exception
	 */
	public final Resource resource(Path path) throws GameException {
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
		for (Path path : resources.keySet()) {
			resources.remove(path).cleanup();
		}
	}

	/**
	 * @return the instance
	 */
	@Contract(pure = true)
	public static ResourceLoader getInstance() {
		return instance;
	}
}
