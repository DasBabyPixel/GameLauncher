package gamelauncher.lwjgl.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;

import gamelauncher.engine.GameException;
import gamelauncher.engine.file.DirectoryStream;
import gamelauncher.engine.file.FileSystem;
import gamelauncher.engine.file.Path;
import gamelauncher.engine.resource.ResourceStream;

public class LWJGLFileSystem implements FileSystem {

	@Override
	public Path getPath(String path) {
		return new LWJGLPath(this, false, path);
	}

	@Override
	public ResourceStream createInputResourceStream(Path path) throws GameException {
		boolean directory = isDirectory(path);
		return new ResourceStream(path, directory, directory ? null : createInputStream(path), null);
	}

	@Override
	public ResourceStream createOutputResourceStream(Path path) throws GameException {
		boolean directory = isDirectory(path);
		return new ResourceStream(path, directory, null, directory ? null : createOutputStream(path));
	}

	@Override
	public void createDirectories(Path path) throws GameException {
		try {
			Files.createDirectories(nio(path));
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

	@Override
	public DirectoryStream createDirectoryStream(Path path) throws GameException {
		ArrayList<Path> files = new ArrayList<>();
		try {
			java.nio.file.DirectoryStream<java.nio.file.Path> ns = Files.newDirectoryStream(nio(path));
			Iterator<java.nio.file.Path> ni = ns.iterator();
			while (ni.hasNext()) {
				java.nio.file.Path np = ni.next();
				files.add(new LWJGLPath(this, false, np.toString()));
			}
			ns.close();
		} catch (IOException ex) {
			throw new GameException(ex);
		} catch (GameException ex) {
			throw ex;
		}
		return new IteratorDirectoryStream(files.iterator());
	}

	@Override
	public boolean isDirectory(Path path) throws GameException {
		return Files.isDirectory(nio(path));
	}

	@Override
	public boolean exists(Path path) throws GameException {
		return Files.exists(nio(path));
	}

	@Override
	public void createFile(Path path) throws GameException {
		try {
			Files.createFile(nio(path));
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

	@Override
	public byte[] readAllBytes(Path path) throws GameException {
		try {
			return Files.readAllBytes(nio(path));
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

	@Override
	public void write(Path path, byte[] bytes) throws GameException {
		try {
			Files.write(nio(path), bytes);
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

	@Override
	public void move(Path path, Path to) throws GameException {
		try {
			Files.move(nio(path), nio(to), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

	@Override
	public InputStream createInputStream(Path path) throws GameException {
		try {
			return Files.newInputStream(nio(path));
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

	@Override
	public OutputStream createOutputStream(Path path) throws GameException {
		try {
			return Files.newOutputStream(nio(path));
		} catch (IOException ex) {
			throw new GameException(ex);
		}
	}

	private java.nio.file.Path nio(Path path) throws GameException {
		if (path instanceof LWJGLPath) {
			return ((LWJGLPath) path).getNio();
		}
		throw new GameException("Invalid path object. Not of type " + LWJGLPath.class.getName());
	}
}
