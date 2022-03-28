package game.settings;

public class SettingPath<T> {

	private final String path;

	public SettingPath(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	
	@Override
	public String toString() {
		return path;
	}
}
