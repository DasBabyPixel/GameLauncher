package gamelauncher.lwjgl.file;

import java.nio.file.Paths;

import gamelauncher.engine.file.AbstractPath;
import gamelauncher.engine.file.FileSystem;

public class LWJGLPath extends AbstractPath {

	private final java.nio.file.Path nio;

	LWJGLPath(FileSystem fileSystem, boolean root, String path) {
		super(fileSystem, root, path);
		this.nio = Paths.get(path);
	}

	public java.nio.file.Path getNio() {
		return nio;
	}

	@Override
	protected AbstractPath newInstance(boolean root, String path) {
		return new LWJGLPath(getFileSystem(), root, path);
	}
}
