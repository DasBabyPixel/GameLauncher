package gamelauncher.engine.util.logging;

/**
 * @author DasBabyPixel
 */
public record LogLevel(String name, int level) {

	public static final LogLevel DEBUG = new LogLevel("DEBUG", -50);
	public static final LogLevel INFO = new LogLevel("INFO", 0);
	public static final LogLevel WARN = new LogLevel("WARN", 50);
	public static final LogLevel ERROR = new LogLevel("ERROR", 100);
	public static final LogLevel STDOUT = new LogLevel("STDOUT", 1);
	public static final LogLevel STDERR = new LogLevel("STDERR", 101);

	/**
	 * @return the name of this {@link LogLevel}
	 */
	@Override
	public String name() {
		return name;
	}

	/**
	 * @return the level of this {@link LogLevel}
	 */
	@Override
	public int level() {
		return level;
	}

	@Override
	public String toString() {
		return name;
	}
}
