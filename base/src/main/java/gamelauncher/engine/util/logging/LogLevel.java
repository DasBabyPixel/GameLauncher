package gamelauncher.engine.util.logging;

import gamelauncher.engine.util.Color;

/**
 * @author DasBabyPixel
 */
public class LogLevel {

	@SuppressWarnings("javadoc")
	public static final LogLevel DEBUG = new LogLevel("DEBUG", -50, new LogColor(150, 150, 150));

	@SuppressWarnings("javadoc")
	public static final LogLevel INFO = new LogLevel("INFO", 0, new LogColor(234, 218, 228));

	@SuppressWarnings("javadoc")
	public static final LogLevel WARN = new LogLevel("WARN", 50, new LogColor(255, 255, 0));

	@SuppressWarnings("javadoc")
	public static final LogLevel ERROR = new LogLevel("ERROR", 100, new LogColor(150, 0, 0));

	@SuppressWarnings("javadoc")
	public static final LogLevel STDOUT = new LogLevel("STDOUT", 1, new LogColor(170, 170, 170));

	@SuppressWarnings("javadoc")
	public static final LogLevel STDERR = new LogLevel("STDERR", 101, new LogColor(180, 0, 0));

	private final String name;

	private final int level;

	private final LogColor displayColor;

	private final LogColor textColor;

	/**
	 * @param name
	 * @param level
	 * @param color
	 */
	public LogLevel(String name, int level, LogColor color) {
		this(name, level, color, color);
	}

	/**
	 * @param name
	 * @param level
	 * @param displayColor
	 * @param textColor
	 */
	public LogLevel(String name, int level, LogColor displayColor, LogColor textColor) {
		super();
		this.name = name;
		this.level = level;
		this.displayColor = displayColor;
		this.textColor = textColor;
	}

	/**
	 * @return the display {@link Color}
	 */
	public LogColor displayColor() {
		return this.displayColor;
	}

	/**
	 * @return the text {@link Color}
	 */
	public LogColor textColor() {
		return this.textColor;
	}

	/**
	 * @return the name of this {@link LogLevel}
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the level of this {@link LogLevel}
	 */
	public int getLevel() {
		return this.level;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
