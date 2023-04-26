package gamelauncher.engine.util.profiler;

import java.util.*;

/**
 * @author DasBabyPixel
 */
public class Profiler {

    private static final int maxgccount = 50;
    private final Map<String, List<SectionHandler>> handlers = new HashMap<>();
    private final List<SectionHandler> defaultHandlers = new ArrayList<>();
    private final ThreadLocal<Deque<Entry>> stacks = ThreadLocal.withInitial(ArrayDeque::new);
    private final Object gclock = new Object();
    private Entry gcentry = null;
    private int gccount = 0;

    /**
     * Begins a section
     *
     * @param type    the profiler section type
     * @param section the section name
     */
    public void begin(String type, String section) {
        Entry entry;
        boolean fromgc = false;
        synchronized (gclock) {
            if (gcentry == null) entry = new Entry(System.nanoTime(), type, section);
            else {
                fromgc = true;
                entry = gcentry;
                gcentry = gcentry.last;
                entry.last = null;
                gccount--;
            }
        }
        if (fromgc) {
            entry.timestamp = System.nanoTime();
            entry.type = type;
            entry.section = section;
        }
        Entry prev = stacks.get().peek();
        if (prev != null) {
            check(prev);
        }
        stacks.get().push(entry);
        synchronized (handlers) {
            if (handlers.containsKey(entry.type)) {
                List<SectionHandler> l = handlers.get(entry.type);
                for (int i = 0; i < l.size(); i++) {
                    l.get(i).handleBegin(type, section);
                }
            }
        }
        synchronized (defaultHandlers) {
            for (int i = 0; i < defaultHandlers.size(); i++) {
                defaultHandlers.get(i).handleBegin(type, section);
            }
        }
    }

    /**
     * Ends a section
     */
    public void end() {
        Entry entry = stacks.get().pop();
        check(entry);
        long nanos = System.nanoTime() - entry.timestamp;

        synchronized (handlers) {
            if (handlers.containsKey(entry.type)) {
                List<SectionHandler> l = handlers.get(entry.type);
                for (int i = 0; i < l.size(); i++) {
                    l.get(i).handleEnd(entry.type, entry.section, nanos);
                }
            }
        }
        synchronized (defaultHandlers) {
            for (int i = 0; i < defaultHandlers.size(); i++) {
                defaultHandlers.get(i).handleEnd(entry.type, entry.section, nanos);
            }
        }
        entry.section = null;
        entry.type = null;
        entry.timestamp = 0;
        synchronized (gclock) {
            if (gccount >= maxgccount) return;
            gccount++;
            entry.last = gcentry;
            gcentry = entry;
        }
    }

    /**
     * Executes all checks in {@link SectionHandler}s
     */
    public void check() {
        check(stacks.get().peek());
    }

    private void check(Entry entry) {
        if (entry == null) return;
        synchronized (handlers) {
            if (handlers.containsKey(entry.type)) {
                List<SectionHandler> l = handlers.get(entry.type);
                for (int i = 0; i < l.size(); i++) {
                    l.get(i).check(entry.type, entry.section);
                }
            }
        }
        synchronized (defaultHandlers) {
            for (int i = 0; i < defaultHandlers.size(); i++) {
                defaultHandlers.get(i).check(entry.type, entry.section);
            }
        }
    }

    /**
     * Begins a section with the default type
     *
     * @param section the section name
     */
    public void begin(String section) {
        Entry e = stacks.get().peek();
        begin(e == null ? "default" : e.type, section);
    }

    /**
     * @param type    the profiler section type
     * @param handler the handler
     */
    public void addHandler(String type, SectionHandler handler) {
        if (type != null) {
            synchronized (handlers) {
                handlers.putIfAbsent(type, new ArrayList<>());
                handlers.get(type).add(handler);
            }
        } else {
            synchronized (defaultHandlers) {
                defaultHandlers.add(handler);
            }
        }
    }

    /**
     * @param type    the profiler section type
     * @param handler the handler
     */
    public void removeHandler(String type, SectionHandler handler) {
        if (type != null) {
            synchronized (handlers) {
                handlers.getOrDefault(type, new ArrayList<>()).remove(handler);
            }
        } else {
            synchronized (defaultHandlers) {
                defaultHandlers.remove(handler);
            }
        }
    }

    /**
     * @param type the profiler section type
     * @return an unmodifiable collection for the handlers
     */
    public Collection<SectionHandler> getHandlers(String type) {
        synchronized (handlers) {
            return Collections.unmodifiableCollection(handlers.getOrDefault(type, Collections.emptyList()));
        }
    }

    private static class Entry {

        Entry last = null;

        long timestamp;
        String type;
        String section;

        public Entry(long timestamp, String type, String section) {
            this.timestamp = timestamp;
            this.type = type;
            this.section = section;
        }
    }
}
