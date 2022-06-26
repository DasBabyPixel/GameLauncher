package gamelauncher.engine.util;

import gamelauncher.engine.plugin.Plugin;

/**
 * @author DasBabyPixel
 */
public class Key {

	@SuppressWarnings("javadoc")
	public final Plugin plugin;
	@SuppressWarnings("javadoc")
	public final String key;

	/**
	 * @param plugin
	 * @param key
	 */
	public Key(Plugin plugin, String key) {
		this.plugin = plugin;
		this.key = key;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the plugin
	 */
	public Plugin getPlugin() {
		return plugin;
	}

	@Override
	public String toString() {
		return String.format("%s:%s", plugin.getName(), key);
	}
}
