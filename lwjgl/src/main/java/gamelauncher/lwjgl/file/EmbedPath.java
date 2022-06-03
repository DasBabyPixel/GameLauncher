package gamelauncher.lwjgl.file;

import gamelauncher.engine.file.AbstractPath;
import gamelauncher.engine.file.FileSystem;

public class EmbedPath extends AbstractPath {

	public static final String prefix = "#";

	public EmbedPath(FileSystem fileSystem, String path) {
		super(fileSystem, addPrefix(path));
	}

	private EmbedPath(FileSystem fileSystem, boolean root, String path) {
		super(fileSystem, root, addPrefix(path));
	}

	@Override
	protected AbstractPath newInstance(boolean root, String path) {
		return new EmbedPath(getFileSystem(), root, path);
	}

	private static String addPrefix(String path) {
		if (path == null) {
			return null;
		}
		if(path.startsWith(prefix)) {
			return path;
		}
		return prefix + path;
	}
}
