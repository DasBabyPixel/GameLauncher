package gamelauncher.engine.data.embed;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;
import java.util.regex.Pattern;

public class EmbedFileSystem extends FileSystem {

    private static final Set<String> supportedFileAttributeViews = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("basic", "embed")));
    private final DataSupplier dataSupplier;
    private final EmbedFileSystemProvider provider;
    private final Path path;
    private volatile boolean isOpen = true;

    public EmbedFileSystem(DataSupplier dataSupplier, EmbedFileSystemProvider provider, Path path) {
        this.dataSupplier = dataSupplier;
        this.provider = provider;
        this.path = path;
    }

    public DataSupplier dataSupplier() {
        return dataSupplier;
    }

    public Path getEmbedPath() {
        return path;
    }

    protected EmbedFileAttributes getAttributes(EmbedPath path) throws IOException {
        boolean directory = isDirectory(path);
        long size = directory ? -1L : size(path);
        return new EmbedFileAttributes(directory, size);
    }

    protected long size(EmbedPath path) throws IOException {
        return dataSupplier.size(path);
//        URL url = cl.getResource(path.toAbsolutePath().toString().substring(1));
//        if (url == null) return -1;
//        URLConnection con;
//        try {
//            con = url.openConnection();
//        } catch (IOException | NullPointerException ex) {
//            System.out.println("ErrorURL: " + path.toAbsolutePath().toString().substring(1));
//            throw ex;
//        }
//        return con.getContentLengthLong();
    }

    protected boolean isDirectory(EmbedPath path) throws IOException {
//        String p = path.toString();
//        if (!p.endsWith("/")) {
//            p = p + "/";
//        }
//        URL url = cl.getResource(p);
//        if (url == null) {
//            return false;
//        }
//        if (url.getProtocol().equals("file")) {
//            try {
//                Path jp = Paths.get(url.toURI());
//                return Files.isDirectory(jp);
//            } catch (URISyntaxException ex) {
//                ex.printStackTrace();
//            }
//        }
//        return true;
        return dataSupplier.directory(path);
    }

    @Override public FileSystemProvider provider() {
        return provider;
    }

    @Override public void close() throws IOException {
        isOpen = false;
    }

    @Override public boolean isOpen() {
        return isOpen;
    }

    @Override public boolean isReadOnly() {
        return true;
    }

    @Override public String getSeparator() {
        return "/";
    }

    @Override public Iterable<Path> getRootDirectories() {
        return Collections.singletonList(path);
    }

    @Override public Iterable<FileStore> getFileStores() {
        return Collections.singleton(new EmbedFileStore(this));
    }

    @Override public PathMatcher getPathMatcher(String s) {
        final int index = s.indexOf(58);
        if (index == -1) {
            throw new IllegalArgumentException();
        }
        final String substring = s.substring(0, index);
        final String substring2 = s.substring(index + 1);
        String regexPattern;
        if (substring.equals("glob")) {
//			regexPattern = ZipUtils.toRegexPattern(substring2);
            throw new UnsupportedOperationException();
        }
        if (!substring.equals("regex")) {
            throw new UnsupportedOperationException("Syntax '" + substring + "' not recognized");
        }
        regexPattern = substring2;
        return new PathMatcher() {

            final Pattern val$pattern = Pattern.compile(regexPattern);

            @Override public boolean matches(final Path path) {
                return this.val$pattern.matcher(path.toString()).matches();
            }

        };
    }

    @Override public Set<String> supportedFileAttributeViews() {
        return supportedFileAttributeViews;
    }

    @Override public Path getPath(String first, String... more) {
        List<String> l = new ArrayList<>(Arrays.asList(first.split("/")));
        for (String m : more) {
            l.addAll(Arrays.asList(m.split("/")));
        }
        boolean absolute = false;
        if (l.get(0).isEmpty()) {
            // Absolute path
            l.remove(0);
        }
        String[] segments = l.toArray(new String[0]);
        return new EmbedPath(this, segments, absolute);
    }

    @Override public UserPrincipalLookupService getUserPrincipalLookupService() {
        throw new UnsupportedOperationException();
    }

    @Override public WatchService newWatchService() {
        throw new UnsupportedOperationException();
    }
}
