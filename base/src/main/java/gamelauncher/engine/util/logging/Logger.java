package gamelauncher.engine.util.logging;

import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicReference;

import gamelauncher.engine.util.logging.SelectiveStream.Output;

/**
 * @author DasBabyPixel
 */
public abstract class Logger {

	/**
	 * The System Streams ({@link System#in} and {@link System#out})
	 */
	public static final SelectiveStream system = new SelectiveStream();
	/**
	 * The {@link AsyncLogStream} for submitting to the {@link #system system stream}
	 */
	public static final AsyncLogStream asyncLogStream = new AsyncLogStream();

	static {
		system.addEntry(System.out, Output.OUT);
		system.addEntry(System.err, Output.ERR);
	}

	private final AtomicReference<StackTraceElement> caller = new AtomicReference<>(null);
	private final AtomicReference<LogLevel> callerLevel = new AtomicReference<>(null);

	/**
	 * @param message
	 */
	public void info(Object message) {
		log(LogLevel.INFO, message);
	}

	/**
	 * @param message
	 * @param args
	 */
	public void infof(String message, Object... args) {
		log(LogLevel.INFO, String.format(message, args));
	}

	/**
	 * @param message
	 */
	public void error(Object message) {
		log(LogLevel.ERROR, message);
	}

	/**
	 * @param message
	 * @param args
	 */
	public void errorf(String message, Object... args) {
		log(LogLevel.ERROR, String.format(message, args));
	}

	/**
	 * @param message
	 */
	public void debug(Object message) {
		log(LogLevel.DEBUG, message);
	}

	/**
	 * @param message
	 * @param args
	 */
	public void debugf(String message, Object... args) {
		log(LogLevel.DEBUG, String.format(message, args));
	}

	/**
	 * @param message
	 */
	public void warn(Object message) {
		log(LogLevel.WARN, message);
	}

	/**
	 * @param message
	 * @param args
	 */
	public void warnf(String message, Object... args) {
		log(LogLevel.WARN, String.format(message, args));
	}

	/**
	 * @param level
	 * @return if the {@link LogLevel} should be displayed
	 */
	public abstract boolean shouldDisplay(LogLevel level);

	/**
	 * @param level
	 * @param message
	 */
	public abstract void log(LogLevel level, Object message);

	/**
	 * @return the {@link PrintStream}
	 */
	public abstract PrintStream createPrintStream();

	/**
	 * @param level
	 * @return the {@link PrintStream}
	 */
	public abstract PrintStream createPrintStream(LogLevel level);

	/**
	 * @return {@link Logger}
	 */
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

	/**
	 * @param clazz
	 * @return {@link Logger}
	 */
	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getSimpleName());
	}

	/**
	 * @param name
	 * @return {@link Logger}
	 */
	public static Logger getLogger(String name) {
		return new SimpleLogger(name, asyncLogStream);
	}
}
