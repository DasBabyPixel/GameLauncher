package gamelauncher.engine.util;

import java.util.Objects;

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
		return this.key;
	}

	/**
	 * @return the plugin
	 */
	public Plugin getPlugin() {
		return this.plugin;
	}

	@Override
	public String toString() {
		return String.format("%s:%s", this.plugin.getName(), this.key);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.key, this.plugin);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		Key other = (Key) obj;
		return Objects.equals(this.key, other.key) && Objects.equals(this.plugin, other.plugin);
	}

}
