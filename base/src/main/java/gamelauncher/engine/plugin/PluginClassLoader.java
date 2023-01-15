package gamelauncher.engine.plugin;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

import gamelauncher.engine.plugin.PluginManager.PluginInfo;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 *
 */
public class PluginClassLoader extends URLClassLoader {

	private final PluginManager pm;
	private final Lock cll;
	final Map<String, Class<?>> classes = new ConcurrentHashMap<>();
	final Collection<Plugin> plugins = ConcurrentHashMap.newKeySet();

	/**
	 * @param parent
	 * @param pm
	 * @param plugin
	 */
	public PluginClassLoader(ClassLoader parent, PluginManager pm, URL plugin) {
		super(new URL[] {
				plugin
		}, parent);
		this.pm = pm;
		this.cll = this.pm.classLoadingLock;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		return findClass(name, true);
	}

	Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
		try {
			cll.lock();
			Class<?> cls = classes.get(name);
			if (cls != null) {
				cll.unlock();
				return cls;
			}
			if (checkGlobal) {
				cls = pm.findClass(name);
			}
			if (cls == null) {
				cls = super.findClass(name);
				if (cls != null) {
					pm.loadedClasses.put(name, cls);
				}
				classes.put(name, cls);
			}
			if (cls != null) {
				return cls;
			}
			throw new ClassNotFoundException(name);
		} finally {
			cll.unlock();
		}
	}

	void pmClose() throws IOException {
		super.close();
		pm.loaders.remove(this);
		for (Plugin pl : plugins) {
			PluginInfo info = pm.infos.get(pl.name());
			info.lock.lock();
			try {
				info.plugin.get().onDisable();
			} catch (GameException ex) {
				ex.printStackTrace();
			}
			info.plugin.set(null);
			info.loader.set(null);
			pm.infos.remove(pl.name());
			info.lock.unlock();
		}
		for (String s : classes.keySet()) {
			pm.loadedClasses.remove(s, classes.get(s));
		}
	}

	static {
		ClassLoader.registerAsParallelCapable();
	}
}
