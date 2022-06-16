package gamelauncher.engine.file.embed;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class EmbedPath implements java.nio.file.Path {

	final EmbedFileSystem fileSystem;
	final String[] segments;
	final boolean absolute;

	public EmbedPath(EmbedFileSystem fileSystem, String[] segments, boolean absolute) {
		this.fileSystem = fileSystem;
		this.segments = segments;
		this.absolute = absolute;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof EmbedPath && Arrays.equals(segments, ((EmbedPath) obj).segments);
	}

	@Override
	public Path getRoot() {
		return new EmbedPath(fileSystem, new String[0], true);
	}

	@Override
	public Path getParent() {
		return new EmbedPath(fileSystem, Arrays.copyOf(segments, segments.length - 1), absolute);
	}

	@Override
	public Path getName(int index) {
		return new EmbedPath(fileSystem, new String[] {
				segments[index]
		}, false);
	}

	@Override
	public Path subpath(int beginIndex, int endIndex) {
		if (beginIndex < 0) {
			throw new IllegalArgumentException();
		}
		if (endIndex >= segments.length) {
			throw new IllegalArgumentException();
		}
		String[] seg = new String[endIndex - beginIndex];
		for (int i = 0; i < seg.length; i++) {
			seg[i] = segments[i + beginIndex];
		}
		return new EmbedPath(fileSystem, seg, false);
	}

	@Override
	public boolean startsWith(Path other) {
		return endsWith(checkPath(other).toString());
	}

	@Override
	public boolean startsWith(String other) {
		return toString().startsWith(other);
	}

	@Override
	public boolean endsWith(Path other) {
		return endsWith(checkPath(other).toString());
	}

	@Override
	public boolean endsWith(String other) {
		return toString().endsWith(other);
	}

	@Override
	public EmbedFileSystem getFileSystem() {
		return fileSystem;
	}

	@Override
	public String toString() {
		if (absolute) {
			return "/" + String.join("/", segments);
		}
		return String.join("/", segments);
	}

	@Override
	public boolean isAbsolute() {
		return absolute;
	}

	@Override
	public Path getFileName() {
		return new EmbedPath(fileSystem, new String[] {
				this.segments[this.segments.length - 1]
		}, false);
	}

	@Override
	public int getNameCount() {
		return segments.length;
	}

	@Override
	public Path normalize() {
		return this;
	}

	@Override
	public Path resolve(Path other) {
		EmbedPath ep = checkPath(other);
		String[] segments = new String[this.segments.length + ep.segments.length];
		for (int i = 0; i < this.segments.length; i++) {
			segments[i] = this.segments[i];
		}
		for (int i = 0; i < ep.segments.length; i++) {
			segments[this.segments.length + i] = ep.segments[i];
		}
		return new EmbedPath(fileSystem, segments, absolute);
	}

	@Override
	public Path resolve(String other) {
		return resolve(getFileSystem().getPath(other));
	}

	@Override
	public Path resolveSibling(Path other) {
		return getParent().resolve(other);
	}

	@Override
	public Path resolveSibling(String other) {
		return getParent().resolve(other);
	}

	@Override
	public Path relativize(Path other) {
		return this;
	}

	@Override
	public EmbedPath toAbsolutePath() {
		if (absolute) {
			return this;
		}
		return new EmbedPath(fileSystem, segments, true);
	}

	@Override
	public Path toRealPath(LinkOption... options) throws IOException {
		return this;
	}

	@Override
	public URI toUri() {
		try {
			return new URI(this.fileSystem.provider().getScheme(),
					this.fileSystem.getEmbedPath().toUri() + "#" + toAbsolutePath().toString(), null);
		} catch (URISyntaxException ex) {
			throw new AssertionError(ex);
		}
	}

	@Override
	public Iterator<Path> iterator() {
		return new Iterator<Path>() {
			private int i = 0;

			@Override
			public boolean hasNext() {
				return i < getNameCount();
			}

			@Override
			public Path next() {
				if (hasNext()) {
					return getName(i++);
				}
				throw new NoSuchElementException();
			}

			@Override
			public void remove() {
				throw new ReadOnlyFileSystemException();
			}
		};
	}

	@Override
	public File toFile() {
		throw new UnsupportedOperationException();
	}

	@Override
	public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers) throws IOException {
		if (watcher != null && events != null && modifiers != null) {
			throw new UnsupportedOperationException();
		}
		throw new NullPointerException();
	}

	@Override
	public WatchKey register(WatchService watcher, Kind<?>... events) throws IOException {
		return this.register(watcher, events, new Modifier[0]);
	}

	@Override
	public int compareTo(Path other) {
		return String.join("/", segments).compareTo(String.join("/", checkPath(other).segments));
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
