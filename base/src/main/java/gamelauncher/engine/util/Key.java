package gamelauncher.engine.util;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Objects;

/**
 * @author DasBabyPixel
 */
public class Key {

    private final String namespace;
    private final String key;

    public Key(@NotNull String namespace, @NotNull String key) {
        this.namespace = namespace;
        this.key = key;
    }

    /**
     * @param plugin the plugin
     * @param key    the key
     */
    public Key(@NotNull Plugin plugin, @NotNull String key) {
        this.namespace = plugin.name();
        this.key = key;
    }

    public Key(String key) {
        this("gamelauncher", key);
    }

    @SuppressWarnings("NewApi") public Path toPath(Path root) {
        Path path = root;
        path = path.resolve(namespace);
        path = path.resolve(key);
        return path;
    }

    /**
     * Returns a new Key with {@link #namespace()} and the given <b>{@code key}</b> as values
     */
    @Api public Key withKey(String key) {
        return new Key(namespace, key);
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
    public String namespace() {
        return this.namespace;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Key other = (Key) obj;
        return Objects.equals(this.key, other.key) && Objects.equals(this.namespace, other.namespace);
    }

    @Override public String toString() {
        return String.format("%s:%s", this.namespace, this.key);
    }
}
