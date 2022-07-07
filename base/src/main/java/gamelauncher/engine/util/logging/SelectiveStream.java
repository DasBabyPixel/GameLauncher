package gamelauncher.engine.util.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author DasBabyPixel
 */
public class SelectiveStream extends OutputStream {

	private final NavigableSet<Entry> entries = Collections.synchronizedNavigableSet(new TreeSet<>());
	private final AtomicReference<Output> output = new AtomicReference<>(null);
	private final AtomicBoolean outputChanged = new AtomicBoolean(false);
	private final AtomicReference<Entry> currentEntry = new AtomicReference<>(null);

	@Override
	public void write(int b) throws IOException {
		computeOutputStream();
		currentEntry.get().out.write(b);
	}

	private void computeOutputStream() {
		if (outputChanged.compareAndSet(true, false)) {
			Output o = output.get();
			this.currentEntry.set(null);
			Iterator<Entry> it = entries.iterator();
			if (it.hasNext()) {
				Entry e = it.next();
				if (e.output.weight <= o.weight) {
					this.currentEntry.set(e);
				}
			}
		}
	}

	/**
	 * @param output
	 */
	public void setOutput(Output output) {
		this.output.set(output);
		this.outputChanged.set(true);
	}

	/**
	 * @param out
	 * @param output
	 */
	public void addEntry(OutputStream out, Output output) {
		entries.add(new Entry(out, output));
		this.outputChanged.set(true);
	}

	/**
	 * @author DasBabyPixel
	 */
	public static class Entry implements Comparable<Entry> {

		@SuppressWarnings("javadoc")
		public final OutputStream out;
		@SuppressWarnings("javadoc")
		public final Output output;

		/**
		 * @param out
		 * @param output
		 */
		public Entry(OutputStream out, Output output) {
			this.out = out;
			this.output = output;
		}

		@Override
		public int compareTo(Entry o) {
			return Integer.compare(output.weight, o.output.weight);
		}
	}

	/**
	 * @author DasBabyPixel
	 */
	public static class Output {
		@SuppressWarnings("javadoc")
		public static final Output OUT = new Output(LogLevel.STDOUT);
		@SuppressWarnings("javadoc")
		public static final Output ERR = new Output(LogLevel.STDERR);

		@SuppressWarnings("javadoc")
		public final int weight;

		/**
		 * @param level
		 */
		public Output(LogLevel level) {
			this(level.getLevel());
		}

		/**
		 * @param weight
		 */
		public Output(int weight) {
			this.weight = weight;
		}
	}
}
