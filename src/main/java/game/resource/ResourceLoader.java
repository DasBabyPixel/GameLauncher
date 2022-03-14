package game.resource;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public abstract class ResourceLoader {

	private static ResourceLoader instance = null;
	private final Map<ResourcePath, Resource> resources = new ConcurrentHashMap<>();
	private final Lock lock = new ReentrantLock();

	public final void set() {
		instance = this;
	}
	
	public final boolean hasResource(ResourcePath path) {
		if(isResourceLoaded(path)) {
			return true;
		}
		return canLoadResource(path);
	}
	
	protected abstract boolean canLoadResource(ResourcePath path);
	
	protected abstract Resource loadResource(ResourcePath path);
	
	public final boolean isResourceLoaded(ResourcePath path) {
		return resources.containsKey(path);
	}
	
	public final Resource getResource(ResourcePath path) {
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
