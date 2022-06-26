package gamelauncher.engine.util.logging;

/**
 * @author DasBabyPixel
 */
@SuppressWarnings("javadoc")
public class LogLevel {

	public static final LogLevel DEBUG = new LogLevel("DEBUG", -50);
	public static final LogLevel INFO = new LogLevel("INFO", 0);
	public static final LogLevel WARN = new LogLevel("WARN", 50);
	public static final LogLevel ERROR = new LogLevel("ERROR", 100);
	public static final LogLevel STDOUT = new LogLevel("STDOUT", 1);
	public static final LogLevel STDERR = new LogLevel("STDERR", 101);

	private final String name;
	private final int level;

	/**
	 * @param name
	 * @param level
	 */
	public LogLevel(String name, int level) {
		this.name = name;
		this.level = level;
	}

	/**
	 * @return the name of this {@link LogLevel}
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the level of this {@link LogLevel}
	 */
	public int getLevel() {
		return level;
	}

	@Override
	public String toString() {
		return name;
	}
}
