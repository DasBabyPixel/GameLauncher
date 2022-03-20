package game.util.logging;

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

public class LogStream extends OutputStream {

	private final PrintStream out;
	private final Logger logger;
	private final Lock lock = new ReentrantLock(true);
	private final DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("HH:mm:ss.SSS")
			.toFormatter();
	private boolean newLine = true;
	private boolean nextNewLine = false;

	public LogStream(Logger logger, PrintStream out) {
		this.logger = logger;
		this.out = out;
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
			printNewLine();
			StackTraceElement t = logger.getCaller();
			if (t != null) {
				out.printf("[%s.%s:%s] ", t.getClassName(), t.getMethodName(), t.getLineNumber());
			}
		}
		out.write(b);
		lock.unlock();
	}

	public void log(LogLevel level, Object message) {
		lock.lock();
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
				logString(level, Objects.toString(message));
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
		printLevel(level);
		println(message);
	}

	private void println(Object message) {
		out.printf("%s%n", message);
	}

	public PrintStream getOutputStream() {
		return out;
	}
}
