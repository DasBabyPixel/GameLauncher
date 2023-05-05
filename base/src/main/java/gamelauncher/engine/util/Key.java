package gamelauncher.engine.util;

import gamelauncher.engine.plugin.Plugin;

import java.nio.file.Path;
import java.util.Objects;

/**
 * @author DasBabyPixel
 */
public class Key {

    private final Plugin plugin;
    private final String key;

    /**
     * @param plugin the plugin
     * @param key    the key
     */
    public Key(Plugin plugin, String key) {
        this.plugin = plugin;
        this.key = plugin == null && key.startsWith("gamelauncher:") ? key.substring("gamelauncher:".length()) : key;
    }

    public Key(String key) {
        this(null, key);
    }

    @SuppressWarnings("NewApi") public Path toPath(Path root) {
        Path path = root;
        if (plugin != null) path = path.resolve(plugin.name());
        path = path.resolve(key);
        return path;
    }

    /**
     * @return the key
     */
    public String key() {
        return this.key;
    }

    /**
     * @return the plugin
     */
    public Plugin plugin() {
        return this.plugin;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Key other = (Key) obj;
        return Objects.equals(this.key, other.key) && Objects.equals(this.plugin, other.plugin);
    }

    @Override public String toString() {
        if (plugin == null) return "gamelauncher:" + this.key;
        return String.format("%s:%s", this.plugin.name(), this.key);
    }
}
