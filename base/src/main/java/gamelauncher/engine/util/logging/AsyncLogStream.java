package gamelauncher.engine.util.logging;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.AbstractQueueSubmissionThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameResource;
import gamelauncher.engine.util.logging.SelectiveStream.Output;

/**
 * @author DasBabyPixel
 */
@SuppressWarnings("javadoc")
public class AsyncLogStream extends AbstractQueueSubmissionThread<AsyncLogStream.LogEntry<?>> implements GameResource {

	private final PrintStream out;
	private final SelectiveStream system;

	private final DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("HH:mm:ss.SSS")
			.toFormatter();

	/**
	 * @param logger
	 */
	public AsyncLogStream() {
		this.system = Logger.system;
		this.setName("AsyncLogStream");
		this.out = new PrintStream(system, false);
	}

	@Override
	protected void startExecuting() {
		system.setOutput(Output.ERR);
		out.println("Starting");
	}

	@Override
	protected void stopExecuting() {
		out.println("Stopping");
	}

	public CompletableFuture<Void> offerLog(Logger logger, LogLevel level, Object message) {
		return submit(new LogEntry<Object>(logger, Thread.currentThread(), message, level));
	}

	public CompletableFuture<Void> offerCalled(LogLevel level, StackTraceElement caller, Object message) {
		return submit(new LogEntry<Object>(null, Thread.currentThread(), message, level, caller));
	}

	@Override
	protected void handleElement(LogEntry<?> element) throws GameException {
		log(element);
	}

	@Override
	public void cleanup() {
		Threads.waitFor(exit());
	}

//	@Override
//	public void write(int b) throws IOException {
//		if (nextNewLine == true) {
//			newLine = true;
//			nextNewLine = false;
//		}
//		if (b == '\n') {
//			nextNewLine = true;
//		}
//		lock.lock();
//		if (newLine == true) {
//			newLine = false;
//			LogLevel level = logger.getCallerLevel();
//			setSystemLevel(level);
//			printNewLine();
//			out.flush();
//			StackTraceElement t = logger.getCaller();
//			if (t != null) {
//				out.printf("[%s] ", level.getName());
//				printThread();
//				if (!t.getClassName().startsWith(Throwable.class.getName())) {
//					out.printf("[%s.%s:%s] ", t.getClassName(), t.getMethodName(), t.getLineNumber());
//				}
//			}
//		}
//		out.write(b);
//		lock.unlock();
//	}

	private void setSystemLevel(LogLevel level) {
		if (level.getLevel() > LogLevel.ERROR.getLevel()) {
			system.setOutput(Output.ERR);
		} else {
			system.setOutput(Output.OUT);
		}
	}

	private void log(LogEntry<?> entry) {
		Object message = entry.object;
		if (message == null) {
			message = "null";
		}
		if (message instanceof Throwable) {
			Throwable t = (Throwable) message;
			StackTraceElement[] stackTrace = t.getStackTrace();
			for (StackTraceElement trace : stackTrace) {
				logString(entry, trace.toString());
			}
		} else {
			if (message.getClass().isArray()) {
				logArray(entry.withObject((Object[]) message));
			} else if (message instanceof Collection<?>) {
				logCollection(entry.withObject((Collection<?>) message));
			} else {
				for (String s1 : Objects.toString(message).split(System.lineSeparator())) {
					for (String s : s1.split("\\n")) {
						logString(entry, s);
					}
				}
			}
		}
	}

	private <T> void logArray(LogEntry<T[]> entry) {
		logString(entry,
				String.format("%s[%s]", entry.object.getClass().getComponentType().getName(), entry.object.length));
		for (T t : entry.object) {
			logString(entry, String.format(" - %s", Objects.toString(t)));
		}
	}

	private void logCollection(LogEntry<Collection<?>> entry) {
		logString(entry, String.format("%s<?> (Size: %s)", entry.object.getClass().getName(), entry.object.size()));
		for (Object t : entry.object) {
			logString(entry, String.format(" - %s", Objects.toString(t)));
		}
	}

	private void printThread(Thread thread) {
		out.printf("[%s] ", thread.getName());
	}

	private void printLoggerName(Logger logger) {
		out.printf("[%s] ", logger.toString());
	}

	private void printNewLine() {
		out.printf("newline");
		out.printf("[%s] ", formatter.format(LocalDateTime.now()));
	}

	private void printLevel(LogLevel level) {
		out.printf("[%s] ", level.getName());
	}

	private void logString(LogEntry<?> parent, String string) {
		logString(parent.withObject(string));
	}

	private void logString(LogEntry<String> entry) {
		setSystemLevel(entry.level);
		for (String s1 : entry.object.split(System.lineSeparator())) {
			for (String object : s1.split("\\n")) {
//				out.println("String---");
				printNewLine();
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
				out.printf("1\"%s2\"%n", object);
				out.println("1\"" + object + "\"2");
			}
		}
	}

	static class LogEntry<T> {
		private final Logger logger;
		private final Thread thread;
		private final T object;
		private final LogLevel level;
		private final StackTraceElement caller;

		public LogEntry(Logger logger, Thread thread, T object, LogLevel level) {
			this(logger, thread, object, level, null);
		}

		public LogEntry(Logger logger, Thread thread, T object, LogLevel level, StackTraceElement caller) {
			super();
			this.logger = logger;
			this.thread = thread;
			this.object = object;
			this.level = level;
			this.caller = caller;
		}

		public <V> LogEntry<V> withObject(V object) {
			return new LogEntry<V>(logger, thread, object, level, caller);
		}
	}
}
