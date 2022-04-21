package gamelauncher.lwjgl.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import gamelauncher.engine.GameException;
import gamelauncher.engine.file.FileSystem;
import gamelauncher.engine.file.Path;

public class LWJGLFileSystem implements FileSystem {

	@Override
	public Path getPath(String path) {
		return new LWJGLPath(path);
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

	private java.nio.file.Path nio(Path path) throws GameException {
		if (path instanceof LWJGLPath) {
			return ((LWJGLPath) path).getNio();
		}
		throw new GameException("Invalid path object. Not of type LWJGLPath");
	}
}
