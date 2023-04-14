package gamelauncher.engine.io.embed;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class EmbedFileAttributeView implements BasicFileAttributeView {

    private final EmbedPath path;

    private final boolean isZipView;

    private EmbedFileAttributeView(final EmbedPath path, final boolean isZipView) {
        this.path = path;
        this.isZipView = isZipView;
    }

    @Override
    public String name() {
        return this.isZipView ? "embed" : "basic";
    }

    @Override
    public EmbedFileAttributes readAttributes() throws IOException {
        return this.path.getFileSystem().getAttributes(path);
    }

    @Override
    public void setTimes(final FileTime fileTime, final FileTime fileTime2, final FileTime fileTime3)
            throws IOException {
        throw new UnsupportedOperationException();
//		this.path.setTimes(fileTime, fileTime2, fileTime3);
    }

    Object attribute(final AttrID attrID, final EmbedFileAttributes zipFileAttributes) {
        switch (attrID) {
            case size: {
                return zipFileAttributes.size();
            }
            case creationTime: {
                return zipFileAttributes.creationTime();
            }
            case lastAccessTime: {
                return zipFileAttributes.lastAccessTime();
            }
            case lastModifiedTime: {
                return zipFileAttributes.lastModifiedTime();
            }
            case isDirectory: {
                return zipFileAttributes.isDirectory();
            }
            case isRegularFile: {
                return zipFileAttributes.isRegularFile();
            }
            case isSymbolicLink: {
                return zipFileAttributes.isSymbolicLink();
            }
            case isOther: {
                return zipFileAttributes.isOther();
            }
            case fileKey: {
                return zipFileAttributes.fileKey();
            }
        }
        return null;
    }

    Map<String, Object> readAttributes(final String anObject) throws IOException {
        final EmbedFileAttributes attributes = this.readAttributes();
        final LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<String, Object>();
        if ("*".equals(anObject)) {
            for (final AttrID attrID : AttrID.values()) {
                try {
                    linkedHashMap.put(attrID.name(), this.attribute(attrID, attributes));
                } catch (IllegalArgumentException ignored) {
                }
            }
        } else {
            for (final String key : anObject.split(",")) {
                try {
                    linkedHashMap.put(key, this.attribute(AttrID.valueOf(key), attributes));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        return linkedHashMap;
    }

    void setAttribute(final String str, final Object o) throws IOException {
        try {
            if (AttrID.valueOf(str) == AttrID.lastModifiedTime) {
                this.setTimes((FileTime) o, null, null);
            }
            if (AttrID.valueOf(str) == AttrID.lastAccessTime) {
                this.setTimes(null, (FileTime) o, null);
            }
            if (AttrID.valueOf(str) == AttrID.creationTime) {
                this.setTimes(null, null, (FileTime) o);
            }
        } catch (IllegalArgumentException ex) {
            throw new UnsupportedOperationException("'" + str + "' is unknown or read-only attribute");
        }
    }

    @SuppressWarnings("unchecked")
    static <V extends FileAttributeView> V get(final EmbedPath zipPath, final Class<V> clazz) {
        if (clazz == null) {
            throw new NullPointerException();
        }
        if (clazz == BasicFileAttributeView.class) {
            return (V) new EmbedFileAttributeView(zipPath, false);
        }
        if (clazz == EmbedFileAttributeView.class) {
            return (V) new EmbedFileAttributeView(zipPath, true);
        }
        return null;
    }

    static EmbedFileAttributeView get(final EmbedPath zipPath, final String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        if (s.equals("basic")) {
            return new EmbedFileAttributeView(zipPath, false);
        }
        if (s.equals("embed")) {
            return new EmbedFileAttributeView(zipPath, true);
        }
        return null;
    }

    private enum AttrID {
        size,
        creationTime,
        lastAccessTime,
        lastModifiedTime,
        isDirectory,
        isRegularFile,
        isSymbolicLink,
        isOther,
        fileKey,
    }

}
