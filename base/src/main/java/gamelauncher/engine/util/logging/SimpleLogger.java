package gamelauncher.engine.util.logging;

import java.io.PrintStream;

public class SimpleLogger extends Logger {

	private int level = 0;
	private final String name;
	private final LogStream logStream;

	public SimpleLogger(String name) {
		this.name = name;
		this.logStream = new LogStream(this);
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
