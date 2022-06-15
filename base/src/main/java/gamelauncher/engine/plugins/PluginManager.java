package gamelauncher.engine.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipInputStream;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.file.DirectoryStream;
import gamelauncher.engine.file.Path;
import gamelauncher.engine.util.logging.Logger;

public class PluginManager {

	private static final Logger logger = Logger.getLogger();
	private final GameLauncher launcher;

	private final Map<String, PluginInfo> infos = new ConcurrentHashMap<>();

	public PluginManager(GameLauncher launcher) {
		this.launcher = launcher;
	}

	public void loadPlugins(Path folder) throws GameException {
		DirectoryStream stream = folder.getFileSystem().createDirectoryStream(folder);
		Iterator<Path> it = stream.iterator();
		while (it.hasNext()) {
			Path path = it.next();
			if (!path.getFileSystem().isDirectory(path)) {
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
		logger.infof("Loading plugins in %s", plugin.getPath());
		try {
			InputStream in = plugin.getFileSystem().createInputStream(plugin);
			URL source = this.getClass().getProtectionDomain().getCodeSource().getLocation();
			StreamClassLoader scl = new StreamClassLoader(this.getClass().getClassLoader(), source,
					new ZipInputStream(in, StandardCharsets.UTF_8));
			for (String key : scl.getClassData().keySet()) {

				Class<?> cls = Class.forName(key, true, scl);
				if (!cls.isAnnotationPresent(Plugin.GamePlugin.class)) {
					continue;
				}
				Object instance = cls.getConstructor().newInstance();
				if (!(instance instanceof Plugin)) {
					continue;
				}
				Plugin pl = (Plugin) instance;
				pl.setLauncher(launcher);
				String name = pl.getName();
				logger.infof("Loading plugin %s - %s", name, key);
				PluginInfo info;
				if (infos.containsKey(name)) {
					info = infos.get(name);
					info.lock.lock();
					info.plugin.get().onDisable();
					info.plugin.set(pl);
					info.lock.unlock();
				} else {
					info = new PluginInfo(name);
					info.lock.lock();
					infos.put(name, info);
					info.plugin.set(pl);
				}
				pl.onEnable();
				info.lock.unlock();

			}
		} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
			throw new GameException(ex);
		}
	}

	public static class PluginInfo {
		private final String name;
		private final Lock lock = new ReentrantLock(true);
		private final AtomicReference<Plugin> plugin = new AtomicReference<>();
		private final AtomicReference<StreamClassLoader> loader = new AtomicReference<>();

		public PluginInfo(String name) {
			this.name = name;
		}

	}
}
