package gamelauncher.engine.util.logging;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import gamelauncher.engine.util.logging.AsyncLogStream.LogEntry;

/**
 * Utility class for handling {@link Throwable}s in logging
 * 
 * @author DasBabyPixel
 */
class ErrorStream extends PrintStream implements ConverterStream {

	private final AsyncLogStream stream;

	private LogEntry<?> entry;

	/**
	 * @param stream
	 */
	public ErrorStream(AsyncLogStream stream) {
		super(new LogStreamConverter(StandardCharsets.UTF_8), true);
		((LogStreamConverter) this.out).converterStream = this;
		this.stream = stream;
	}

	public void log(LogEntry<?> entry) {
		this.stream.setSystemLevel(entry.level);
		this.entry = entry;
		entry.throwable.printStackTrace(this);
	}

	@Override
	public void converted(String line) {
		stream.printNewLine(entry.time);
		stream.printLoggerName(entry.logger);
		stream.printThread(entry.thread);
		stream.out.printf("%s%n", line);
	}

//	public void write(int b) throws IOException {
//		boolean newLine = b == '\n';
//		if (nextNewLine) {
//			nextNewLine = false;
//			stream.printNewLine(time);
//			stream.printLoggerName(logger);
//			stream.printThread(thread);
//		}
//		if (newLine) {
//			nextNewLine = true;
//		}
//		stream.out.write(b);
//	}

}
