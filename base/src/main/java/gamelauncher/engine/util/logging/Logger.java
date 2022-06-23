package gamelauncher.engine.util.logging;

import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicReference;

import gamelauncher.engine.util.logging.SelectiveStream.Output;

public abstract class Logger {

	public static final SelectiveStream system = new SelectiveStream();
	
	static {
		system.addEntry(System.out, Output.OUT);
		system.addEntry(System.err, Output.ERR);
	}

	private final AtomicReference<StackTraceElement> caller = new AtomicReference<>(null);
	private final AtomicReference<LogLevel> callerLevel = new AtomicReference<>(null);

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
		log(LogLevel.ERROR, String.format(message, args));
	}

	public void debug(Object message) {
		log(LogLevel.DEBUG, message);
	}

	public void debugf(String message, Object... args) {
		log(LogLevel.DEBUG, String.format(message, args));
	}

	public void warn(Object message) {
		log(LogLevel.WARN, message);
	}

	public void warnf(String message, Object... args) {
		log(LogLevel.WARN, String.format(message, args));
	}

	public abstract boolean shouldDisplay(LogLevel level);

	public abstract void log(LogLevel level, Object message);

	public abstract PrintStream createPrintStream();

	public abstract PrintStream createPrintStream(LogLevel level);

	public void setCaller(StackTraceElement caller) {
		this.caller.set(caller);
	}

	public void setCallerLevel(LogLevel level) {
		this.callerLevel.set(level);
	}

	public LogLevel getCallerLevel() {
		return this.callerLevel.get();
	}

	public StackTraceElement getCaller() {
		return this.caller.get();
	}

	@SuppressWarnings("null")
	public static Logger getLogger() {
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		StackTraceElement caller = null;
		String cname = Logger.class.getName();
		boolean next = false;
		for (int i = 0; i < st.length; i++) {
			StackTraceElement t = st[i];
			if (t.getClassName().equals(cname)) {
				next = true;
			} else if (next) {
				next = false;
				caller = t;
			}
		}
		int index = caller.getClassName().lastIndexOf('.');
		return getLogger(caller.getClassName().substring(index == -1 ? 0 : index + 1));
//		int index = t.getClassName().indexOf('.');
//		return getLogger(t.getClassName().substring(index == -1 ? 0 : index));
	}

	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getSimpleName());
	}

	public static Logger getLogger(String name) {
		return new SimpleLogger(name);
	}
}
