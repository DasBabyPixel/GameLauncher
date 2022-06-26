package gamelauncher.engine.settings;

import java.util.Objects;

/**
 * @author DasBabyPixel
 */
public class SettingPath {

	private final String path;

	/**
	 * @param path
	 */
	public SettingPath(String path) {
		this.path = path;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
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
		SettingPath other = (SettingPath) obj;
		return Objects.equals(path, other.path);
	}
}
