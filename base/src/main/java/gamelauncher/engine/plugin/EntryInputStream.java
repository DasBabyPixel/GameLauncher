package gamelauncher.engine.plugin;

import gamelauncher.engine.data.Files;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import java8.util.concurrent.CompletableFuture;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class EntryInputStream extends AbstractGameResource implements AutoCloseable {

    public final boolean directory;
    public final Path path;
    public final ZipInputStream zip;
    public final Iterator<Path> dir;
    private volatile ZipEntry currentEntry = null;
    private volatile ZipEntry nextEntry = null;

    public EntryInputStream(Path path) throws GameException {
        this.path = path;
        directory = Files.isDirectory(path);
        zip = directory ? null : new ZipInputStream(Files.newInputStream(path), StandardCharsets.UTF_8);
        dir = directory ? Files.walk(path).iterator() : null;
    }

    public boolean hasNextEntry() throws IOException {
        if (directory) {
            return dir.hasNext();
        }
        if (nextEntry == null) {
            nextEntry = zip.getNextEntry();
        }
        return nextEntry != null;
    }

    public Entry nextEntry() throws GameException {
        if (directory) {
            return new Entry(path.relativize(dir.next()).toString());
        }
        if (nextEntry == null) {
            try {
                nextEntry = zip.getNextEntry();
            } catch (IOException ex) {
                throw new GameException(ex);
            }
        }
        currentEntry = nextEntry;
        nextEntry = null;
        return new Entry(currentEntry.getName());
    }

    @Override public void close() throws GameException {
        cleanup();
    }

    @Override public CompletableFuture<Void> cleanup0() throws GameException {
        if (!directory) {
            try {
                zip.close();
            } catch (IOException ex) {
                throw new GameException(ex);
            }
        }
        return null;
    }

    public static class Entry {
        public final String name;

        public Entry(String name) {
            this.name = name;
        }

        public String name() {
            return name;
        }
    }
}
