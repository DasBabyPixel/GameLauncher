package gamelauncher.engine.resource;

import java.util.Objects;

public class ResourcePath {

	private final String path;

	public ResourcePath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public ResourceStream newResourceStream() {
		return ResourceLoader.getInstance().getResource(this).newResourceStream();
	}

	public String getFileName() {
		int index = path.lastIndexOf('/');
		if (index == -1) {
			return path;
		}
		return path.substring(index, path.length());
	}

	@Override
	public String toString() {
		return path;
	}

	@Override
	public int hashCode() {
		return Objects.hash(path);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResourcePath other = (ResourcePath) obj;
		return Objects.equals(path, other.path);
	}
}
