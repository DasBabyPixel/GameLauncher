package gamelauncher.engine.file.embed;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@SuppressWarnings("javadoc")
public class EmbedFileSystem extends FileSystem {

	private static final Set<String> supportedFileAttributeViews = Collections
			.unmodifiableSet(new HashSet<>(Arrays.asList("basic", "embed")));

	private final EmbedFileSystemProvider provider;
	private volatile boolean isOpen = true;
	private final Path path;
	public final ClassLoader cl;

	public EmbedFileSystem(ClassLoader cl, EmbedFileSystemProvider provider, Path path) {
		this.cl = cl;
		this.provider = provider;
		this.path = path;
	}

	public Path getEmbedPath() {
		return path;
	}

	EmbedFileAttributes getAttributes(EmbedPath path) throws IOException {
		boolean directory = isDirectory(path);
		long size = directory ? -1L : size(path);
		EmbedFileAttributes efa = new EmbedFileAttributes(directory, size);
		return efa;
	}

	long size(EmbedPath path) throws IOException {
		URL url = cl.getResource(path.toAbsolutePath().toString().substring(1));
		URLConnection con = url.openConnection();
		long s = con.getContentLengthLong();
		return s;
	}

	boolean isDirectory(EmbedPath path) {
		String p = path.toString();
		if (!p.endsWith("/")) {
			p = p + "/";
		}
		URL url = cl.getResource(p);
		if (url == null) {
			return false;
		}
		if (url.getProtocol().equals("file")) {
			try {
				Path jp = Paths.get(url.toURI());
				return Files.isDirectory(jp);
			} catch (URISyntaxException ex) {
				ex.printStackTrace();
			}

		}
		return url != null;
	}

	@Override
	public FileSystemProvider provider() {
		return provider;
	}

	@Override
	public void close() throws IOException {
		isOpen = false;
	}

	@Override
	public boolean isOpen() {
		return isOpen;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public String getSeparator() {
		return "/";
	}

	@Override
	public Iterable<Path> getRootDirectories() {
		return Arrays.asList(path);
	}

	@Override
	public Iterable<FileStore> getFileStores() {
		return Arrays.asList(new EmbedFileStore(this));
	}

	@Override
	public PathMatcher getPathMatcher(String s) {
		final int index = s.indexOf(58);
		if (index <= 0 || index == s.length()) {
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
			@Override
			public boolean matches(final Path path) {
				return this.val$pattern.matcher(path.toString()).matches();
			}

			final Pattern val$pattern = Pattern.compile(regexPattern);
		};
	}

	@Override
	public Set<String> supportedFileAttributeViews() {
		return supportedFileAttributeViews;
	}

	@Override
	public Path getPath(String first, String... more) {
		List<String> l = new ArrayList<>();
		l.addAll(Arrays.asList(first.split("/")));
		for (String m : more) {
			l.addAll(Arrays.asList(m.split("/")));
		}
		if (l.get(0).isEmpty()) {
			// Absolute path
			l.remove(0);
		}
		String[] segments = l.toArray(new String[l.size()]);
		return new EmbedPath(this, segments, true);
	}

	@Override
	public UserPrincipalLookupService getUserPrincipalLookupService() {
		throw new UnsupportedOperationException();
	}

	@Override
	public WatchService newWatchService() throws IOException {
		throw new UnsupportedOperationException();
	}

}
