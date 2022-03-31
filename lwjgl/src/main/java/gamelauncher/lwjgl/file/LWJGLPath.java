package gamelauncher.lwjgl.file;

import java.nio.file.Paths;

import gamelauncher.engine.file.Path;

public class LWJGLPath implements Path {

	private final String path;
	private final java.nio.file.Path nio;

	public LWJGLPath(String path) {
		this(path, Paths.get(path));
	}

	public LWJGLPath(String path, java.nio.file.Path nio) {
		this.path = path;
		this.nio = nio;
	}

	@Override
	public Path resolve(String path) {
		return new LWJGLPath(this.path + Path.SEPERATOR + path, nio.resolve(path));
	}

	@Override
	public String getPath() {
		return path;
	}

	public java.nio.file.Path getNio() {
		return nio;
	}
}
