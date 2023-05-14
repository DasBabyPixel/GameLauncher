package gamelauncher.engine.util.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author DasBabyPixel
 */
public class SelectiveStream extends OutputStream {

    private final Set<Entry> entries = Collections.synchronizedSet(new TreeSet<>());
    private final AtomicReference<Output> output = new AtomicReference<>(null);
    private final AtomicBoolean outputChanged = new AtomicBoolean(false);
    private final AtomicReference<Entry> currentEntry = new AtomicReference<>(null);

    @Override public void write(int b) throws IOException {
        this.computeOutputStream();
        this.currentEntry.get().out.write(b);
    }

    private void computeOutputStream() throws IOException {
        if (this.outputChanged.compareAndSet(true, false)) {
            Output o = this.output.get();
            Entry c = this.currentEntry.getAndSet(null);
            if (c != null) c.out.flush();
            Iterator<Entry> it = this.entries.iterator();
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
     * @return a new {@link OutputStream} for the given {@link Output}
     */
    public OutputStream computeOutputStream(Output output) {
        OutputStream out = null;
        Iterator<Entry> it = this.entries.iterator();
        if (it.hasNext()) {
            Entry e = it.next();
            if (e.output.weight <= output.weight) {
                out = e.out;
            }
        }
        return out;
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
        this.entries.add(new Entry(out, output));
        this.outputChanged.set(true);
    }

    /**
     * @author DasBabyPixel
     */
    static class Entry implements Comparable<Entry> {

        public final OutputStream out;

        public final Output output;

        /**
         * @param out
         * @param output
         */
        public Entry(OutputStream out, Output output) {
            this.out = out;
            this.output = output;
        }

        @Override public int compareTo(Entry o) {
            return Integer.compare(this.output.weight, o.output.weight);
        }

    }

    /**
     * @author DasBabyPixel
     */
    public static class Output {

        /**
         * Standard OUT output
         */
        public static final Output OUT = new Output(LogLevel.STDOUT);

        /**
         * Standard ERR output
         */
        public static final Output ERR = new Output(LogLevel.STDERR);

        /**
         * The weight of the output
         */
        public final int weight;

        /**
         * @param level
         */
        public Output(LogLevel level) {
            this(level.level());
        }

        /**
         * @param weight
         */
        public Output(int weight) {
            this.weight = weight;
        }

    }

}
