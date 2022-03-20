package game.util.logging;

public class LogLevel {

	public static final LogLevel DEBUG = new LogLevel("DEBUG", -50);
	public static final LogLevel INFO = new LogLevel("INFO", 0);
	public static final LogLevel WARN = new LogLevel("DEBUG", 50);
	public static final LogLevel ERROR = new LogLevel("DEBUG", 100);

	private final String name;
	private final int level;

	public LogLevel(String name, int level) {
		this.name = name;
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	@Override
	public String toString() {
		return name;
	}
}
