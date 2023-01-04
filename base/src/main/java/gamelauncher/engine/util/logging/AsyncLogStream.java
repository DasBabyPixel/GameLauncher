package gamelauncher.engine.util.logging;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.AbstractQueueSubmissionThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.logging.SelectiveStream.Output;

/**
 * @author DasBabyPixel
 */
public class AsyncLogStream extends AbstractQueueSubmissionThread<AsyncLogStream.LogEntry<?>> {

	final PrintStream out;

	final SelectiveStream system;

	final ErrorStream errorStream;

	private final DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("HH:mm:ss.SSS")
			.toFormatter();

	public AsyncLogStream() {
		this.system = Logger.system;
		this.setName("AsyncLogStream");
		this.out = new PrintStream(system, false);
		this.errorStream = new ErrorStream(this);
	}

	@Override
	protected void startExecuting() {
	}

	@Override
	protected void stopExecuting() {
	}

	public CompletableFuture<Void> offerLog(Logger logger, LogLevel level, Object message) {
		if (!exit) {
			return submit(new LogEntry<>(logger, Thread.currentThread(), message, level));
		}
		log(new LogEntry<>(logger, Thread.currentThread(), message, level));
		return CompletableFuture.completedFuture(null);
	}

	public CompletableFuture<Void> offerCalled(LogLevel level, StackTraceElement caller, Object message) {
		if (!exit) {
			return submit(new LogEntry<>(null, Thread.currentThread(), message, level, caller));
		}
		log(new LogEntry<>(null, Thread.currentThread(), message, level, caller));
		return CompletableFuture.completedFuture(null);
	}

	@Override
	protected void handleElement(LogEntry<?> element) {
		log(element);
	}

	@Override
	public void cleanup0() throws GameException {
		Threads.waitFor(exit());
	}

	void setSystemLevel(LogLevel level) {
		if (level.level() > LogLevel.ERROR.level()) {
			system.setOutput(Output.ERR);
		} else {
			system.setOutput(Output.OUT);
		}
	}

	void log(LogEntry<?> entry) {
		Object message = entry.object;
		if (message == null) {
			message = "null";
		}
		if (entry.isThrowable) {
			errorStream.log(entry);
		} else {
			if (message.getClass().isArray()) {
				assert message instanceof Object[];
				logArray(entry.withObject((Object[]) message));
			} else if (message instanceof Collection<?>) {
				logCollection(entry.withObject((Collection<?>) message));
			} else {
				logString(entry, Objects.toString(message));
			}
		}
	}

	<T> void logArray(LogEntry<T[]> entry) {
		logString(entry,
				String.format("%s[%s]", entry.object.getClass().getComponentType().getName(), entry.object.length));
		for (T t : entry.object) {
			logString(entry, String.format(" - %s", t));
		}
	}

	void logCollection(LogEntry<Collection<?>> entry) {
		logString(entry, String.format("%s<?> (Size: %s)", entry.object.getClass().getName(), entry.object.size()));
		for (Object t : entry.object) {
			logString(entry, String.format(" - %s", t));
		}
	}

	void printThread(Thread thread) {
		out.printf("[%s] ", thread.getName());
	}

	void printLoggerName(Logger logger) {
		out.printf("[%s] ", logger.toString());
	}

	void printNewLine(TemporalAccessor time) {
		out.printf("[%s] ", formatter.format(time));
	}

	void printLevel(LogLevel level) {
		out.printf("[%s] ", level.name());
	}

	void logString(LogEntry<?> parent, String string) {
		logString(parent.withObject(string));
	}

	void logString(LogEntry<String> entry) {
		setSystemLevel(entry.level);
		for (String s1 : entry.object.split(System.lineSeparator())) {
			for (String object : s1.split("\\n")) {
				printNewLine(entry.time);
				if (entry.caller == null) {
					printLoggerName(entry.logger);
					printThread(entry.thread);
					printLevel(entry.level);
				} else {
					printLevel(entry.level);
					printThread(entry.thread);
					out.printf("[%s.%s:%s] ", entry.caller.getClassName(), entry.caller.getMethodName(),
							entry.caller.getLineNumber());
				}
				out.printf("%s%n", object);
			}
		}
	}

	static class LogEntry<T> {

		final Logger logger;

		final Thread thread;

		final T object;

		final LogLevel level;

		final StackTraceElement caller;

		final TemporalAccessor time;

		final boolean isThrowable;

		final Throwable throwable;

		public LogEntry(Logger logger, Thread thread, T object, LogLevel level) {
			this(logger, thread, object, level, null);
		}

		public LogEntry(Logger logger, Thread thread, T object, LogLevel level, StackTraceElement caller) {
			this(logger, thread, object, level, caller, LocalDateTime.now());
		}

		public LogEntry(Logger logger, Thread thread, T object, LogLevel level, StackTraceElement caller,
				TemporalAccessor time) {
			this.logger = logger;
			this.thread = thread;
			this.object = object;
			this.level = level;
			this.caller = caller;
			this.time = time;
			this.isThrowable = object instanceof Throwable;
			this.throwable = this.isThrowable ? (Throwable) object : null;
		}

		public <V> LogEntry<V> withObject(V object) {
			return new LogEntry<>(logger, thread, object, level, caller, time);
		}

	}

}
