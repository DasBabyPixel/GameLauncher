package gamelauncher.engine.util.logging;

import java.io.PrintStream;

/**
 * @author DasBabyPixel
 */
public class SimpleLogger extends Logger {

	private int level = 0;
	private final String name;
	private final AsyncLogStream logStream;

	/**
	 * @param name
	 * @param stream 
	 */
	public SimpleLogger(String name, AsyncLogStream stream) {
		this.name = name;
		this.logStream =stream;
	}

	@Override
	public boolean shouldDisplay(LogLevel level) {
		return level.getLevel() >= this.level;
	}

	@Override
	public void log(LogLevel level, Object message) {
		if (shouldDisplay(level)) {
			logStream.offerLog(this, level, message);
		}
	}

	@Override
	public PrintStream createPrintStream() {
		return createPrintStream(LogLevel.STDOUT);
	}

	@Override
	public PrintStream createPrintStream(LogLevel level) {
		return new CallerPrintStream(level, this, logStream);
	}

	@Override
	public String toString() {
		return name;
	}
}
