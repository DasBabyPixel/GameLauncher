package gamelauncher.lwjgl.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import gamelauncher.engine.GameException;
import gamelauncher.engine.file.DirectoryStream;
import gamelauncher.engine.file.FileSystem;
import gamelauncher.engine.file.Path;
import gamelauncher.engine.resource.ResourceStream;

public class EmbedFileSystem implements FileSystem {

	private final ClassLoader cl;
	private final boolean zipFile;
	private final String[] entries;

	public EmbedFileSystem() throws GameException {
		this.cl = getClass().getClassLoader();
		try {
			
			java.nio.file.Path path = Paths
					.get(EmbedFileSystem.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			this.zipFile = !Files.isDirectory(path);
			if (zipFile) {
				ZipFile f = new ZipFile(path.toFile());
				Enumeration<? extends ZipEntry> it = f.entries();
				List<String> el = new ArrayList<>();
				while (it.hasMoreElements()) {
					ZipEntry e = it.nextElement();
					el.add(e.getName());
				}
				entries = el.toArray(new String[el.size()]);
			} else {
				entries = new String[0];
			}
		} catch (URISyntaxException | IOException ex) {
			throw new GameException(ex);
		}
	}

	@Override
	public DirectoryStream createDirectoryStream(Path path) throws GameException {
		try {
			ArrayList<Path> files = new ArrayList<>();
			if (zipFile) {
				String s = convert(path);
				for (String entry : entries) {
					if (entry.startsWith(s)) {
						int li = entry.lastIndexOf('/');
						if (li == s.length() - 1 || (entry.lastIndexOf('/') == entry.length() - 1
								&& entry.substring(s.length()).indexOf('/') == entry.lastIndexOf('/'))) {
							files.add(new EmbedPath(this, entry));
						}
					}
				}
			} else {
				URI uri = cl.getResource(convert(path)).toURI();
				java.nio.file.Path folder = Paths.get(uri);
				java.nio.file.DirectoryStream<java.nio.file.Path> nds = Files.newDirectoryStream(folder);
				nds.forEach(p -> {
					files.add(new EmbedPath(this, Files.isDirectory(p) ? p.toString() + "/" : p.toString()));
				});
				nds.close();
			}
			return new IteratorDirectoryStream(files.iterator());
		} catch (URISyntaxException | IOException ex) {
			throw new GameException(ex);
		}
	}

	@Override
	public boolean isDirectory(Path path) throws GameException {
		String p = path.getPath();
		if (!p.endsWith("/")) {
			p = p + "/";
		}
		return cl.getResource(p) != null;
	}

	@Override
	public ResourceStream createInputResourceStream(Path path) throws GameException {
		return new ResourceStream(path, false, createInputStream(path), null);
	}

	@Override
	public boolean exists(Path path) throws GameException {
		checkFileSystem(path);
		return cl.getResource(convert(path)) != null;
	}

	@Override
	public byte[] readAllBytes(Path path) throws GameException {
		try (ResourceStream stream = new ResourceStream(path, false, createInputStream(path), null)) {
			byte[] bytes = stream.readAllBytes();
			return bytes;
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

	@Override
	public InputStream createInputStream(Path path) throws GameException {
		checkFileSystem(path);
		return cl.getResourceAsStream(convert(path));
	}

	@Override
	public Path getPath(String path) {
		return new EmbedPath(this, path);
	}

	private String convert(Path path) {
		return path.getPath().substring(EmbedPath.prefix.length());
	}

	private void checkFileSystem(Path path) throws GameException {
		if (path.getFileSystem() != this) {
			throw new GameException("Wrong FileSystem: " + path.getFileSystem());
		}
	}

	@Override
	public ResourceStream createOutputResourceStream(Path path) throws GameException {
		return unsupported();
	}

	@Override
	public void createDirectories(Path path) throws GameException {
		unsupported();
	}

	@Override
	public void createFile(Path path) throws GameException {
		unsupported();
	}

	@Override
	public OutputStream createOutputStream(Path path) throws GameException {
		return unsupported();
	}

	@Override
	public void write(Path path, byte[] bytes) throws GameException {
		unsupported();
	}

	@Override
	public void move(Path path, Path to) throws GameException {
		unsupported();
	}

	private <T> T unsupported() throws GameException {
		throw new GameException(new UnsupportedOperationException());
	}
}
