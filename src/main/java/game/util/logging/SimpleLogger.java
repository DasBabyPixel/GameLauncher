package game.util.logging;

import java.io.PrintStream;

public class SimpleLogger extends Logger {

	private int level = 0;
	private final String name;
	private final LogStream logStream;

	public SimpleLogger(String name) {
		this.name = name;
		this.logStream = new LogStream(this, System.out);
	}

	@Override
	public boolean shouldDisplay(LogLevel level) {
		return level.getLevel() >= this.level;
	}

	@Override
	public void log(LogLevel level, Object message) {
		if (shouldDisplay(level)) {
			logStream.log(level, message);
		}
	}

	@Override
	public PrintStream createPrintStream() {
		return new CallerPrintStream(this, logStream);
	}

	@Override
	public String toString() {
		return name;
	}
}
