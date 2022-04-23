package gamelauncher.engine.resource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import gamelauncher.engine.GameException;
import gamelauncher.engine.file.Path;

public abstract class ResourceLoader {

	private static ResourceLoader instance = null;
	private final Map<Path, Resource> resources = new ConcurrentHashMap<>();
	private final Lock lock = new ReentrantLock();

	public final void set() {
		instance = this;
	}
	
	public final boolean hasResource(Path path) throws GameException {
		if(isResourceLoaded(path)) {
			return true;
		}
		return canLoadResource(path);
	}
	
	protected abstract boolean canLoadResource(Path path) throws GameException;
	
	protected abstract Resource loadResource(Path path);
	
	public final boolean isResourceLoaded(Path path) {
		return resources.containsKey(path);
	}
	
	public final Resource getResource(Path path) {
		if(isResourceLoaded(path)) {
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
	
	public static ResourceLoader getInstance() {
		return instance;
	}
}
