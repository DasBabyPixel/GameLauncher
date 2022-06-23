package gamelauncher.engine.util;

import gamelauncher.engine.plugin.Plugin;

public class Key {

	public final Plugin plugin;
	public final String key;

	public Key(Plugin plugin, String key) {
		this.plugin = plugin;
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	@Override
	public String toString() {
		return String.format("%s:%s", plugin.getName(), key);
	}
}
