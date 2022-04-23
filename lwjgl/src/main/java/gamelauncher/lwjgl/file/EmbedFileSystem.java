package gamelauncher.lwjgl.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gamelauncher.engine.GameException;
import gamelauncher.engine.file.FileSystem;
import gamelauncher.engine.file.Path;
import gamelauncher.engine.resource.ResourceStream;

public class EmbedFileSystem implements FileSystem {

	private final ClassLoader cl;

	public EmbedFileSystem() {
		this.cl = getClass().getClassLoader();
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
