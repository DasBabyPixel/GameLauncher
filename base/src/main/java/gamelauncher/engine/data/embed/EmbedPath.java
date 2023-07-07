package gamelauncher.engine.data.embed;

import java8.util.Objects;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author DasBabyPixel
 */
public class EmbedPath implements java.nio.file.Path {

    final EmbedFileSystem fileSystem;
    final String[] segments;
    final boolean absolute;
    final boolean root;

    EmbedPath(EmbedFileSystem fileSystem, String[] segments, boolean absolute) {
        this.fileSystem = fileSystem;
        this.segments = segments;
        this.absolute = absolute;
        this.root = false;
    }

    EmbedPath(EmbedFileSystem fileSystem, String[] segments, boolean absolute, boolean root) {
        this.fileSystem = fileSystem;
        this.segments = segments;
        this.absolute = absolute;
        this.root = root;
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(this.segments);
        result = prime * result + Objects.hash(this.absolute, this.root);
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        EmbedPath other = (EmbedPath) obj;
        return this.absolute == other.absolute && this.root == other.root && Arrays.equals(this.segments, other.segments);
    }

    @Override public Path getRoot() {
        return new EmbedPath(this.fileSystem, new String[0], true);
    }

    @Override public Path getParent() {
        if (this.absolute) {
            if (this.segments.length == 1) {
                return new EmbedPath(this.fileSystem, new String[0], true, true);
            }
            if (this.root) {
                return null;
            }
        }
        return new EmbedPath(this.fileSystem, Arrays.copyOf(this.segments, this.segments.length - 1), this.absolute);
    }

    @Override public Path getName(int index) {
        return new EmbedPath(this.fileSystem, new String[]{this.segments[index]}, false);
    }

    @Override public Path subpath(int beginIndex, int endIndex) {
        if (beginIndex < 0) {
            throw new IllegalArgumentException();
        }
        if (endIndex >= this.segments.length) {
            throw new IllegalArgumentException();
        }
        String[] seg = new String[endIndex - beginIndex];
        System.arraycopy(this.segments, beginIndex, seg, 0, seg.length);
        return new EmbedPath(this.fileSystem, seg, false);
    }

    @Override public boolean startsWith(@NotNull Path other) {
        return this.endsWith(this.checkPath(other).toString());
    }

    @Override public boolean startsWith(@NotNull String other) {
        return this.toString().startsWith(other);
    }

    @Override public boolean endsWith(@NotNull Path other) {
        return this.endsWith(this.checkPath(other).toString());
    }

    @Override public boolean endsWith(@NotNull String other) {
        return this.toString().endsWith(other);
    }

    @Override public EmbedFileSystem getFileSystem() {
        return this.fileSystem;
    }

    @Override public String toString() {
        if (this.absolute) {
            return "/" + String.join("/", this.segments);
        }
        return String.join("/", this.segments);
    }

    @Override public boolean isAbsolute() {
        return this.absolute;
    }

    @Override public Path getFileName() {
        return new EmbedPath(this.fileSystem, new String[]{this.segments[this.segments.length - 1]}, false);
    }

    @Override public int getNameCount() {
        return this.segments.length;
    }

    @Override public Path normalize() {
        return this;
    }

    @Override public Path resolve(@NotNull Path other) {
        EmbedPath ep = this.checkPath(other);
        if (ep.isAbsolute()) {
            return ep;
        }
        if (ep.segments.length == 0) {
            return this;
        }
        String[] segments = new String[this.segments.length + ep.segments.length];
        System.arraycopy(this.segments, 0, segments, 0, this.segments.length);
        System.arraycopy(ep.segments, 0, segments, this.segments.length, ep.segments.length);
        return new EmbedPath(this.fileSystem, segments, this.absolute);
    }

    @Override public Path resolve(@NotNull String other) {
        return this.resolve(this.getFileSystem().getPath(other));
    }

    @Override public Path resolveSibling(@NotNull Path other) {
        return this.getParent().resolve(other);
    }

    @Override public Path resolveSibling(@NotNull String other) {
        return this.getParent().resolve(other);
    }

    @Override public Path relativize(@NotNull Path other) {
        return this;
    }

    @Override public EmbedPath toAbsolutePath() {
        if (this.absolute) {
            return this;
        }
        return new EmbedPath(this.fileSystem, this.segments, true);
    }

    @Override public Path toRealPath(LinkOption @NotNull ... options) {
        return this;
    }

    @Override public URI toUri() {
        try {
            return new URI(this.fileSystem.provider().getScheme(), this.fileSystem.getEmbedPath().toUri() + "#" + this.toAbsolutePath(), null);
        } catch (URISyntaxException ex) {
            throw new AssertionError(ex);
        }
    }

    @Override public Iterator<Path> iterator() {
        return new Iterator<>() {

            private int i = 0;

            @Override public boolean hasNext() {
                return this.i < EmbedPath.this.getNameCount();
            }

            @Override public Path next() {
                if (this.hasNext()) {
                    return EmbedPath.this.getName(this.i++);
                }
                throw new NoSuchElementException();
            }

            @Override public void remove() {
                throw new ReadOnlyFileSystemException();
            }

        };
    }

    @Override public File toFile() {
        throw new UnsupportedOperationException();
    }

    @Override public WatchKey register(@NotNull WatchService watcher, Kind<?> @NotNull [] events, Modifier... modifiers) throws IOException {
        if (modifiers != null) {
            throw new UnsupportedOperationException();
        }
        throw new NullPointerException();
    }

    @Override public WatchKey register(@NotNull WatchService watcher, Kind<?> @NotNull ... events) throws IOException {
        return this.register(watcher, events, new Modifier[0]);
    }

    @Override public int compareTo(@NotNull Path other) {
        return String.join("/", this.segments).compareTo(String.join("/", this.checkPath(other).segments));
    }

    private EmbedPath checkPath(Path path) {
        if (path == null) {
            throw new NullPointerException();
        }
        if (!(path instanceof EmbedPath)) {
            throw new ProviderMismatchException();
        }
        return (EmbedPath) path;
    }

}
