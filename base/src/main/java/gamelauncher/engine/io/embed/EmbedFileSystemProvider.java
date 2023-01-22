package gamelauncher.engine.io.embed;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class EmbedFileSystemProvider extends FileSystemProvider {

	private volatile EmbedFileSystem embedFileSystem;

	@Override
	public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type,
			LinkOption... options) {
		return EmbedFileAttributeView.get(toEmbedPath(path), type);
	}

	@Override
	public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type,
			LinkOption... options) throws IOException {
		return type != BasicFileAttributes.class && type != EmbedFileAttributes.class
				? null
				: type.cast(embedFileSystem.getAttributes(toEmbedPath(path)));
	}

	@Override
	public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options,
			FileAttribute<?>... attrs) throws IOException {
		EmbedPath embedPath = toEmbedPath(path);
		InputStream in = embedFileSystem.cl.getResourceAsStream(
				embedPath.toAbsolutePath().toString().substring(1));
		if (in == null) {
			throw new FileNotFoundException(path.toString());
		}
		long size = embedFileSystem.size(toEmbedPath(path));
		return new SeekableByteChannel() {
			final ReadableByteChannel rbc = Channels.newChannel(in);
			long read = 0L;

			@Override
			public boolean isOpen() {
				return rbc.isOpen();
			}

			@Override
			public void close() throws IOException {
				rbc.close();
			}

			@Override
			public int write(ByteBuffer src) {
				throw new UnsupportedOperationException();
			}

			@Override
			public SeekableByteChannel truncate(long size) {
				throw new NonWritableChannelException();
			}

			@Override
			public long size() {
				return size;
			}

			@Override
			public int read(ByteBuffer dst) throws IOException {
				final int read = this.rbc.read(dst);
				if (read > 0) {
					this.read += read;
				}
				return read;
			}

			@Override
			public SeekableByteChannel position(long newPosition) {
				throw new UnsupportedOperationException();
			}

			@Override
			public long position() {
				return read;
			}
		};
	}

	@Override
	public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter)
			throws IOException {
		Enumeration<URL> en = embedFileSystem.cl.getResources(toEmbedPath(dir).toString());
		Iterator<Path> it = new Iterator<>() {
			@Override
			public boolean hasNext() {
				return en.hasMoreElements();
			}

			@Override
			public Path next() {
				return new EmbedPath(embedFileSystem, new String[0], true);
			}
		};
		return new DirectoryStream<Path>() {
			@Override
			public void close() throws IOException {
			}

			@Override
			public Iterator<Path> iterator() {
				return it;
			}
		};
	}

	@Override
	public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options)
			throws IOException {
		int index = attributes.indexOf(':');
		String type;
		String substring2;
		if (index == -1) {
			type = "basic";
			substring2 = attributes;
		} else {
			type = attributes.substring(0, index++);
			substring2 = attributes.substring(index);
		}
		final EmbedFileAttributeView value = EmbedFileAttributeView.get(toEmbedPath(path), type);
		if (value == null) {
			throw new UnsupportedOperationException("view not supported");
		}
		return value.readAttributes(substring2);
	}

	@Override
	public void setAttribute(Path path, String attribute, Object o, LinkOption... options)
			throws IOException {
		int index = attribute.indexOf(58);
		String substring;
		String substring2;
		if (index == -1) {
			substring = "basic";
			substring2 = attribute;
		} else {
			substring = attribute.substring(0, index++);
			substring2 = attribute.substring(index);
		}
		final EmbedFileAttributeView value =
				EmbedFileAttributeView.get(toEmbedPath(path), substring);
		if (value == null) {
			throw new UnsupportedOperationException("view <null> is not supported");
		}
		value.setAttribute(substring2, o);
	}

	@Override
	public String getScheme() {
		return "embed";
	}

	public EmbedPath toEmbedPath(Path path) {
		if (path == null) {
			throw new NullPointerException();
		}
		if (path instanceof EmbedPath) {
			return (EmbedPath) path;
		}
		throw new ProviderMismatchException();
	}

	@Override
	public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
		if (embedFileSystem != null) {
			throw new FileSystemAlreadyExistsException();
		}
		try {
			Path path = Paths.get(
					getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
			ClassLoader cl = getClass().getClassLoader();
			embedFileSystem = new EmbedFileSystem(cl, this, path);
		} catch (URISyntaxException ex) {
			throw new IOException(ex);
		}
		return embedFileSystem;
	}

	@Override
	public FileSystem getFileSystem(URI uri) {
		return embedFileSystem;
	}

	@Override
	public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Path path) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void copy(Path source, Path target, CopyOption... options) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void move(Path source, Path target, CopyOption... options) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSameFile(Path path, Path path2) throws IOException {
		return path.toRealPath().equals(path2.toRealPath());
	}

	@Override
	public boolean isHidden(Path path) throws IOException {
		return false;
	}

	@Override
	public FileStore getFileStore(Path path) throws IOException {
		return new EmbedFileStore(embedFileSystem);
	}

	@Override
	public void checkAccess(Path path, AccessMode... modes) {
	}

	@Override
	public Path getPath(URI uri) {
		String s = uri.getSchemeSpecificPart();
		int index = s.indexOf("#/");
		if (index == -1) {
			throw new IllegalArgumentException();
		}
		return getFileSystem(uri).getPath(s.substring(index + 1));
	}

}
