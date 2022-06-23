package gamelauncher.engine.util.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SelectiveStream extends OutputStream {

//	public final OutputStream out;
//	public final OutputStream err;
	private final NavigableSet<Entry> entries = Collections.synchronizedNavigableSet(new TreeSet<>());
	private final AtomicReference<Output> output = new AtomicReference<>(null);
	private final AtomicBoolean outputChanged = new AtomicBoolean(false);
	private final AtomicReference<Entry> currentEntry = new AtomicReference<>(null);

	public SelectiveStream() {
//		this.out = out;
//		this.err = err;
	}

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

	public void setOutput(Output output) {
		this.output.set(output);
		this.outputChanged.set(true);
	}

	public void addEntry(OutputStream out, Output output) {
		entries.add(new Entry(out, output));
		this.outputChanged.set(true);
	}

	public static class Entry implements Comparable<Entry> {

		public final OutputStream out;
		public final Output output;

		public Entry(OutputStream out, Output output) {
			this.out = out;
			this.output = output;
		}

		@Override
		public int compareTo(Entry o) {
			return Integer.compare(output.weight, o.output.weight);
		}
	}

	public static class Output {
		public static final Output OUT = new Output(LogLevel.STDOUT);
		public static final Output ERR = new Output(LogLevel.STDERR);

		public final int weight;

		public Output(LogLevel level) {
			this(level.getLevel());
		}

		public Output(int weight) {
			this.weight = weight;
		}
	}
}
