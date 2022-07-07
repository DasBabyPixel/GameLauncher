package gamelauncher.engine.util.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import gamelauncher.engine.util.logging.SelectiveStream.Output;

/**
 * @author DasBabyPixel
 */
public class LogStream extends OutputStream {

	private final PrintStream out;
	private final SelectiveStream system;

	private final Logger logger;
	/**
	 * The lock of this LogStream
	 */
	public final Lock lock = new ReentrantLock(true);
	private final DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("HH:mm:ss.SSS")
			.toFormatter();
	private boolean newLine = true;
	private boolean nextNewLine = false;

	/**
	 * @param logger
	 */
	public LogStream(Logger logger) {
		this.logger = logger;
		this.system = Logger.system;
		this.out = new PrintStream(system, false);
	}

	@Override
	public void write(int b) throws IOException {
		if (nextNewLine == true) {
			newLine = true;
			nextNewLine = false;
		}
		if (b == '\n') {
			nextNewLine = true;
		}
		lock.lock();
		if (newLine == true) {
			newLine = false;
			LogLevel level = logger.getCallerLevel();
			setSystemLevel(level);
			printNewLine();
			out.flush();
			StackTraceElement t = logger.getCaller();
			if (t != null) {
				out.printf("[%s] ", level.getName());
				printThread();
				if (!t.getClassName().startsWith(Throwable.class.getName())) {
					out.printf("[%s.%s:%s] ", t.getClassName(), t.getMethodName(), t.getLineNumber());
				}
			}
		}
		out.write(b);
		lock.unlock();
	}

	private void setSystemLevel(LogLevel level) {
		if (level.getLevel() > LogLevel.ERROR.getLevel()) {
			system.setOutput(Output.ERR);
		} else {
			system.setOutput(Output.OUT);
		}
	}

	/**
	 * @param level
	 * @param message
	 */
	public void log(LogLevel level, Object message) {
		lock.lock();
		setSystemLevel(level);
		if (message == null) {
			message = "null";
		}
		if (message instanceof Throwable) {
			Throwable t = (Throwable) message;
			StackTraceElement[] stackTrace = t.getStackTrace();
			for (StackTraceElement trace : stackTrace) {
				logString(level, trace.toString());
			}
		} else {
			if (message.getClass().isArray()) {
				logArray(level, (Object[]) message);
			} else if (message instanceof Collection<?>) {
				logCollection(level, (Collection<?>) message);
			} else {
				for (String s1 : Objects.toString(message).split(System.lineSeparator())) {
					for (String s : s1.split("\\n")) {
						logString(level, s);
					}
				}
			}
		}
		lock.unlock();
	}

	private <T> void logArray(LogLevel level, T[] array) {
		logString(level, String.format("%s[%s]", array.getClass().getComponentType().getName(), array.length));
		for (T t : array) {
			logString(level, String.format(" - %s", Objects.toString(t)));
		}
	}

	private void logCollection(LogLevel level, Collection<?> collection) {
		logString(level, String.format("%s<?> (Size: %s)", collection.getClass().getName(), collection.size()));
		for (Object t : collection) {
			logString(level, String.format(" - %s", Objects.toString(t)));
		}
	}

	private void printThread() {
		out.printf("[%s] ", Thread.currentThread().getName());
	}

	private void printLoggerName() {
		out.printf("[%s] ", logger.toString());
	}

	private void printNewLine() {
		out.printf("[%s] ", formatter.format(LocalDateTime.now()));
	}

	private void printLevel(LogLevel level) {
		out.printf("[%s] ", level.getName());
	}

	private void logString(LogLevel level, String message) {
		printNewLine();
		printLoggerName();
		printThread();
		printLevel(level);
		out.printf("%s%n", message);
	}

	/**
	 * @return the {@link PrintStream}
	 */
	public PrintStream getOutputStream() {
		return out;
	}
}
