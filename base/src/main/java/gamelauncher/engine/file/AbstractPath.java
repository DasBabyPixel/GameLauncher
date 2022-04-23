package gamelauncher.engine.file;

import java.util.Objects;

public abstract class AbstractPath implements Path {

	private final boolean root;
	private final String path;
	private final FileSystem fileSystem;

	public AbstractPath(FileSystem fileSystem, String path) {
		this(fileSystem, false, path);
	}

	public AbstractPath(FileSystem fileSystem, boolean root, String path) {
		this.fileSystem = fileSystem;
		this.root = root;
		this.path = path;
	}

	@Override
	public final Path getParent() {
		int index = path.lastIndexOf(Path.SEPERATOR);
		if (index == -1) {
			return newInstance(true, null);
		}
		return newInstance(false, path.substring(0, index));
	}

	@Override
	public final Path resolve(String path) {
		if (root) {
			return newInstance(false, path);
		}
		return newInstance(false, this.path + Path.SEPERATOR + path);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Path) {
			Path p = (Path) obj;
			String ppath = p.getPath();
			if (ppath == null && path == null) {
				return obj.getClass().equals(this.getClass());
			}
			if (ppath == null || path == null) {
				return false;
			}
			return path.equals(ppath);
		}
		return super.equals(obj);
	}
	
	@Override
	public FileSystem getFileSystem() {
		return fileSystem;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(path);
	}

	public final boolean isRoot() {
		return root;
	}

	@Override
	public final String getPath() {
		return path;
	}

	protected abstract AbstractPath newInstance(boolean root, String path);
}
