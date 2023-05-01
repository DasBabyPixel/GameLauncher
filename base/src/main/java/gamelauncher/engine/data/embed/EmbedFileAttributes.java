package gamelauncher.engine.data.embed;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

@SuppressWarnings("javadoc")
public class EmbedFileAttributes implements BasicFileAttributes {

    private final boolean directory;
    private final long size;

    public EmbedFileAttributes(boolean directory, long size) {
        this.directory = directory;
        this.size = size;
    }

    @Override public FileTime lastModifiedTime() {
        return FileTime.fromMillis(0L);
    }

    @Override public FileTime lastAccessTime() {
        return FileTime.fromMillis(0L);
    }

    @Override public FileTime creationTime() {
        return FileTime.fromMillis(0L);
    }

    @Override public boolean isRegularFile() {
        return !directory;
    }

    @Override public boolean isDirectory() {
        return directory;
    }

    @Override public boolean isSymbolicLink() {
        return false;
    }

    @Override public boolean isOther() {
        return false;
    }

    @Override public long size() {
        return size;
    }

    @Override public Object fileKey() {
        return null;
    }
}
