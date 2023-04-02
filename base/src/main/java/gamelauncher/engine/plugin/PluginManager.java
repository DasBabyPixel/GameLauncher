package gamelauncher.engine.plugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.io.Files;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.logging.Logger;

/**
 * @author DasBabyPixel
 */
public class PluginManager {

	private static final Logger logger = Logger.logger();

	private final GameLauncher launcher;

	final Map<String, PluginInfo> infos = new ConcurrentHashMap<>();

	final Collection<PluginClassLoader> loaders = ConcurrentHashMap.newKeySet();

	final Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();

	final Lock classLoadingLock = new ReentrantLock();

	final Lock pluginLoadLock = new ReentrantLock(true);

	/**
	 * @param launcher
	 */
	public PluginManager(GameLauncher launcher) {
		this.launcher = launcher;
	}

	/**
	 * Loads all the plugins in a folder
	 *
	 * @param folder
	 *
	 * @throws GameException
	 */
	public void loadPlugins(Path folder) throws GameException {
		logger.debugf("Loading plugin files in folder %s",
				folder.toAbsolutePath().normalize().toString());
		DirectoryStream<Path> stream = Files.newDirectoryStream(folder);
		Iterator<Path> it = stream.iterator();
		while (it.hasNext()) {
			Path path = it.next();
			if (!Files.isDirectory(path)) {
				loadPlugin(path);
			}
		}
	}

	/**
	 * Loads plugins
	 *
	 * @param plugins
	 *
	 * @throws GameException
	 */
	public void loadPlugins(Collection<Path> plugins) throws GameException {
		for (Path path : plugins) {
			loadPlugin(path);
		}
	}

	/**
	 * Loads the given plugin
	 *
	 * @param plugin the plugin to load
	 *
	 * @throws GameException when some error happens
	 */
	public void loadPlugin(Path plugin) throws GameException {
		logger.debugf("Loading plugins in %s", plugin.toAbsolutePath().normalize().toString());
		try {
			PluginClassLoader pcl =
					new PluginClassLoader(Thread.currentThread().getContextClassLoader(), this,
							plugin.toUri().toURL());
			Collection<Class<?>> pluginClasses = new HashSet<>();
			try (EntryInputStream ein = new EntryInputStream(plugin)) {
				EntryInputStream.Entry e;
				while (ein.hasNextEntry()) {
					e = ein.nextEntry();

					if (!e.name().endsWith(".class") || e.name().startsWith("META-INF") || e.name()
							.equals("module-info.class")) {
						continue;
					}
					String className =
							e.name().substring(0, e.name().length() - 6).replace("\\", "/")
									.replace("/", ".");
					Class<?> ocls = loadedClasses.get(className);
					if (ocls != null) {
						PluginClassLoader opcl = (PluginClassLoader) ocls.getClassLoader();
						if (opcl != pcl) {
							opcl.pmClose();
						}
					}

					Class<?> cls = Class.forName(className, true, pcl);
					if (!cls.isAnnotationPresent(Plugin.GamePlugin.class)) {
						continue;
					}
					pluginClasses.add(cls);

				}
			} catch (IllegalAccessError ex) {
				throw new GameException(ex);
			}
			for (Class<?> cls : pluginClasses) {
				String className = cls.getName();
				Object instance = cls.getConstructor().newInstance();
				if (!(instance instanceof Plugin)) {
					continue;
				}
				Plugin pl = (Plugin) instance;
				pcl.plugins.add(pl);
				pl.launcher(launcher);
				String name = pl.name();
				logger.infof("Loading plugin %s - %s", name, className);
				PluginInfo info;
				if (infos.containsKey(name)) {
					throw new IllegalAccessError();
				}
				info = new PluginInfo(name);
				info.lock.lock();
				infos.put(name, info);
				info.plugin.set(pl);
				info.loader.set(pcl);
				try {
					pl.onEnable();
				} catch (GameException ex) {
					ex.printStackTrace();
				}
				info.lock.unlock();
			}
		} catch (IOException | ClassNotFoundException | InstantiationException |
				IllegalAccessException | IllegalArgumentException | InvocationTargetException |
				NoSuchMethodException | SecurityException ex) {
			throw new GameException(ex);
		}
	}

	/**
	 * Unloads all plugins
	 *
	 * @throws GameException an exception
	 */
	public void unloadPlugins() throws GameException {
		for (Plugin plugin : this.infos.values().stream().map(i -> i.plugin.get())
				.collect(Collectors.toList())) {
			unloadPlugin(plugin);
		}
	}

	/**
	 * Unloads a plugin
	 *
	 * @param plugin
	 *
	 * @throws GameException
	 */
	public void unloadPlugin(Plugin plugin) throws GameException {
		PluginInfo info = this.infos.get(plugin.name());
		PluginClassLoader pcl = info.loader.get();
		try {
			pcl.pmClose();
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

	Class<?> findClass(String name) {
		Class<?> cls = loadedClasses.get(name);
		if (cls != null) {
			return cls;
		}
		for (PluginClassLoader pcl : loaders) {
			try {
				cls = pcl.findClass(name, false);
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			}
			if (cls != null) {
				break;
			}
		}
		return cls;
	}

	/**
	 * @author DasBabyPixel
	 */
	public static class PluginInfo {

		final String name;

		final Lock lock = new ReentrantLock(true);

		final AtomicReference<Plugin> plugin = new AtomicReference<>();

		final AtomicReference<PluginClassLoader> loader = new AtomicReference<>();

		/**
		 * @param name an exception
		 */
		public PluginInfo(String name) {
			this.name = name;
		}

	}

}
