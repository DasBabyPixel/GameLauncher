package gamelauncher.engine.util.logging;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fusesource.jansi.Ansi;

import gamelauncher.engine.util.Color;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.AbstractQueueSubmissionThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.logging.SelectiveStream.Output;

/**
 * @author DasBabyPixel
 */
@SuppressWarnings("javadoc")
public class AsyncLogStream extends AbstractQueueSubmissionThread<AsyncLogStream.LogEntry<?>> {

	final PrintStream out;

	final SelectiveStream system;

	private final DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("HH:mm:ss.SSS")
			.toFormatter();

	/**
	 * @param logger
	 */
	public AsyncLogStream() {
		this.system = Logger.system;
		this.setName("AsyncLogStream");
		this.out = new PrintStream(this.system, false);
	}

	@Override
	protected void startExecuting() {
	}

	@Override
	protected void stopExecuting() {
	}

	public CompletableFuture<Void> offerLog(Logger logger, LogLevel level, Object message) {
		if (!this.exit) {
			return this.submit(new LogEntry<Object>(logger, Thread.currentThread(), message, level));
		}
		this.log(new LogEntry<Object>(logger, Thread.currentThread(), message, level));
		return CompletableFuture.completedFuture(null);
	}

	public CompletableFuture<Void> offerCalled(LogLevel level, StackTraceElement caller, Object message) {
		if (!this.exit) {
			return this.submit(new LogEntry<Object>(null, Thread.currentThread(), message, level, caller));
		}
		this.log(new LogEntry<Object>(null, Thread.currentThread(), message, level, caller));
		return CompletableFuture.completedFuture(null);
	}

	@Override
	protected void handleElement(LogEntry<?> element) throws GameException {
		this.log(element);
	}

	@Override
	public void cleanup0() throws GameException {
		Threads.waitFor(this.exit());
	}

	void setSystemLevel(LogLevel level) {
		if (level.level() > LogLevel.ERROR.level()) {
			this.system.setOutput(Output.ERR);
		} else {
			this.system.setOutput(Output.OUT);
		}
	}

	private static final Pattern throwablePattern = Pattern.compile("\\(.+\\.java:[0-9]+\\)");

	private void log(LogLevel level, TemporalAccessor time, StackTraceElement caller, Thread thread, Logger logger,
			Formatted f) {
		Object[] data = new Object[f.getObjects().length];
		LogColor resetTo = level.textColor();
		LogColor[] logColors = new LogColor[data.length];
		String[] logColorReplacements = new String[data.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = f.getObjects()[i];
			if (data[i] instanceof Throwable t) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				t.printStackTrace(pw);
				pw.flush();
				pw.close();
				String s = sw.getBuffer().toString();
				Matcher m = AsyncLogStream.throwablePattern.matcher(s.toString());
				StringBuilder sb = new StringBuilder();
				int last = 0;
				while (m.find()) {
					sb.append(s.substring(last, m.start()));
					last = m.end();
					sb.append(Ansi.ansi().reset().a(m.group()));
				}
				data[i] = sb.toString();
			} else if (data[i] instanceof Collection<?> c) {
				StringBuilder sb = new StringBuilder();
				sb.append(c.getClass().getName()).append("<?> (Size: ").append(Integer.toString(c.size())).append(")");
				for (Object o : c) {
					sb.append("\n - ").append(Objects.toString(o));
				}
				data[i] = sb.toString();
			} else if (data[i].getClass().isArray()) {
				Object[] a = (Object[]) data[i];
				StringBuilder sb = new StringBuilder();
				sb.append(a.getClass().componentType().getName()).append('[').append(a.length).append(']');
				for (int j = 0; j < a.length; j++) {
					sb.append("\n - ").append(j).append(": ").append(Objects.toString(a[j]));
				}
				data[i] = sb.toString();
			} else if (data[i] instanceof LogColor c) {
				if (c.color() == null) {
					// Reset
					c = resetTo;
				}
				logColors[i] = c;
				String r = String.format("%%lcr%n", c.hashCode());
				logColorReplacements[i] = r;
				data[i] = r;
			}
		}
		String print = String.format(f.getFormat(), data);
		String[] lines = this.lines(print);

		int lci = 0;
		LogColor lastColor = resetTo;
		for (int i = 0; i < lines.length; i++) {
			lines[i] = this.colorToAnsi(lastColor) + lines[i];
			w1: while (true) {
				if (logColors.length <= lci) {
					break w1;
				}
				while (logColors[lci] == null) {
					lci++;
					continue w1;
				}
				if (lines[i].contains(logColorReplacements[lci])) {
					lines[i] = lines[i].replace(logColorReplacements[lci], this.colorToAnsi(logColors[lci]));
					lastColor = logColors[lci];
					lci++;
					continue w1;
				}
				break w1;
			}
		}
		print = String.join("\n", lines);
		this.logString(level, time, caller, thread, logger, print);
	}

	void log(LogEntry<?> entry) {
		Object message = entry.object;
		if (message == null) {
			message = "null";
		}
		if (message instanceof Formatted f) {
			this.log(entry.level, entry.time, entry.caller, entry.thread, entry.logger, f);
		} else {
			this.logString(LogLevel.ERROR, entry.time, entry.caller, entry.thread, entry.logger,
					"Unsupported logging method. Please upgrade");
			this.logString(entry.level, entry.time, entry.caller, entry.thread, entry.logger,
					Objects.toString(message));
		}
	}

	private String colorToAnsi(LogColor color) {
		return Ansi.ansi()
				.reset()
				.fgRgb(color.color().ired(), color.color().igreen(), color.color().iblue())
				.toString();
	}

	private static final LogColor fgThread = new LogColor(new Color(200, 200, 200, 255));

	private static final LogColor fgLogger = new LogColor(new Color(0, 100, 255, 255));

	private static final LogColor fgTime = new LogColor(new Color(70, 255, 70, 255));

	void printThread(Thread thread) {
		this.out.printf(this.ansi0(AsyncLogStream.fgThread), thread.getName());
	}

	void printLoggerName(Logger logger) {
		this.out.printf(this.ansi0(AsyncLogStream.fgLogger), logger.toString());
	}

	void printTime(TemporalAccessor time) {
		this.out.printf(this.ansi0(AsyncLogStream.fgTime), this.formatter.format(time));
	}

	void printLevel(LogLevel level) {
		this.out.printf(this.ansi0(level.displayColor()), level.name());
	}

	private String ansi0(LogColor color) {
		return Ansi.ansi()
				.fgRgb(100, 100, 100)
				.a("[")
				.fgRgb(color.color().ired(), color.color().igreen(), color.color().iblue())
				.a("%s")
				.fgRgb(100, 100, 100)
				.a("]")
				.reset()
				.a(" ")
				.toString();
	}

	private void logString(LogLevel level, TemporalAccessor time, StackTraceElement caller, Thread thread,
			Logger logger, String string) {
		this.setSystemLevel(level);
		for (String object : this.lines(string)) {
			this.printTime(time);
			if (caller == null) {
				this.printLoggerName(logger);
				this.printThread(thread);
				this.printLevel(level);
			} else {
				this.printLevel(level);
				this.printThread(thread);
				this.out.printf("[%s.%s:%s] ", caller.getClassName(), caller.getMethodName(), caller.getLineNumber());
			}
			this.out.printf(Ansi.ansi().reset().a("%s").reset().a("%n").toString(), object);
		}
	}

	private String[] lines(String input) {
		return input.split("(\\r\\n|\\r|\\n)");
	}

	static class LogEntry<T> {

		final Logger logger;

		final Thread thread;

		final T object;

		final LogLevel level;

		final StackTraceElement caller;

		final TemporalAccessor time;

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
		}

		public <V> LogEntry<V> withObject(V object) {
			return new LogEntry<V>(this.logger, this.thread, object, this.level, this.caller, this.time);
		}

		public LogEntry<T> withLevel(LogLevel level) {
			return new LogEntry<T>(this.logger, this.thread, this.object, level, this.caller, this.time);
		}

	}

}
