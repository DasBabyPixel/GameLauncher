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

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.file.Files;
import gamelauncher.engine.util.logging.Logger;

public class PluginManager {

	private static final Logger logger = Logger.getLogger();
	private final GameLauncher launcher;

	final Map<String, PluginInfo> infos = new ConcurrentHashMap<>();
	final Collection<PluginClassLoader> loaders = ConcurrentHashMap.newKeySet();

	final Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();
	final Lock classLoadingLock = new ReentrantLock();
	final Lock pluginLoadLock = new ReentrantLock(true);

	public PluginManager(GameLauncher launcher) {
		this.launcher = launcher;
	}

	public void loadPlugins(Path folder) throws GameException {
		logger.infof("Loading plugin files in folder %s", folder.toAbsolutePath().normalize().toString());
		DirectoryStream<Path> stream = Files.newDirectoryStream(folder);
		Iterator<Path> it = stream.iterator();
		while (it.hasNext()) {
			Path path = it.next();
			if (!Files.isDirectory(path)) {
				loadPlugin(path);
			}
		}
	}

	public void loadPlugins(Collection<Path> plugins) throws GameException {
		for (Path path : plugins) {
			loadPlugin(path);
		}
	}

	public void loadPlugin(Path plugin) throws GameException {
		logger.infof("Loading plugins in %s", plugin.toAbsolutePath().normalize().toString());
		try {
			PluginClassLoader pcl = new PluginClassLoader(Thread.currentThread().getContextClassLoader(), this,
					plugin.toUri().toURL());
			Collection<Class<?>> pluginClasses = new HashSet<>();
			try (EntryInputStream ein = new EntryInputStream(plugin)) {
				// ZipInputStream zin = new ZipInputStream(Files.newInputStream(plugin),
				// StandardCharsets.UTF_8);
				// ZipEntry ze;
				EntryInputStream.Entry e;
				while (ein.hasNextEntry()) {
					e = ein.getNextEntry();

					System.out.println("Entry: " + e.getName());
					// ze = zin.getNextEntry();
					// if (ze == null)
					// break;
					if (!e.getName().endsWith(".class") || e.getName().startsWith("META-INF")) {
						continue;
					}
					String className = e.getName()
							.substring(0, e.getName().length() - 6)
							.replace("\\", "/")
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
				ex.printStackTrace();
			}
			for (Class<?> cls : pluginClasses) {
				String className = cls.getName();
				Object instance = cls.getConstructor().newInstance();
				if (!(instance instanceof Plugin)) {
					continue;
				}
				Plugin pl = (Plugin) instance;
				pcl.plugins.add(pl);
				pl.setLauncher(launcher);
				String name = pl.getName();
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
				pl.onEnable();
				info.lock.unlock();
			}
		} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
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

	public static class PluginInfo {
		final String name;
		final Lock lock = new ReentrantLock(true);
		final AtomicReference<Plugin> plugin = new AtomicReference<>();
		final AtomicReference<PluginClassLoader> loader = new AtomicReference<>();

		public PluginInfo(String name) {
			this.name = name;
		}

	}
}
