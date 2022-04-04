package gamelauncher.engine.util.logging;

import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Logger {

	private final AtomicReference<StackTraceElement> caller = new AtomicReference<>(null);

	public void info(Object message) {
		log(LogLevel.INFO, message);
	}

	public void infof(String message, Object... args) {
		log(LogLevel.INFO, String.format(message, args));
	}

	public void error(Object message) {
		log(LogLevel.ERROR, message);
	}

	public void errorf(String message, Object... args) {
		log(LogLevel.INFO, String.format(message, args));
	}

	public void debug(Object message) {
		log(LogLevel.DEBUG, message);
	}

	public void debugf(String message, Object... args) {
		log(LogLevel.INFO, String.format(message, args));
	}

	public void warn(Object message) {
		log(LogLevel.WARN, message);
	}

	public void warnf(String message, Object... args) {
		log(LogLevel.INFO, String.format(message, args));
	}

	public abstract boolean shouldDisplay(LogLevel level);

	public abstract void log(LogLevel level, Object message);

	public abstract PrintStream createPrintStream();

	public void setCaller(StackTraceElement caller) {
		this.caller.set(caller);
	}

	public StackTraceElement getCaller() {
		return this.caller.get();
	}

	public static Logger getLogger() {
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		StackTraceElement t = st[st.length - 2];
		int index = t.getClassName().indexOf('.');
		return getLogger(t.getClassName().substring(index == -1 ? 0 : index));
	}

	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getSimpleName());
	}

	public static Logger getLogger(String name) {
		return new SimpleLogger(name);
	}
}
