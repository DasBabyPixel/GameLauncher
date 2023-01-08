package gamelauncher.engine.util.profiler;

import gamelauncher.engine.util.Stack;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author DasBabyPixel
 */
public class Profiler {

	private final Map<String, Collection<SectionHandler>> handlers = new ConcurrentHashMap<>();
	private final Collection<SectionHandler> defaultHandlers = ConcurrentHashMap.newKeySet();
	private final ThreadLocal<Stack<Entry>> stacks = ThreadLocal.withInitial(Stack::new);

	/**
	 * Begins a section
	 *
	 * @param type    the profiler section type
	 * @param section the section name
	 */
	public void begin(String type, String section) {
		Entry entry = new Entry(System.nanoTime(), type, section);
		Entry prev = stacks.get().peek();
		if (prev != null) {
			check(prev);
		}
		stacks.get().push(entry);
		foreach(entry, h -> h.handleBegin(type, section));
	}

	/**
	 * Ends a section
	 */
	public void end() {
		Entry entry = stacks.get().pop();
		check(entry);
		long nanos = System.nanoTime() - entry.timestamp;
		foreach(entry, h -> h.handleEnd(entry.type, entry.section, nanos));
	}

	/**
	 * Executes all checks in {@link SectionHandler}s
	 */
	public void check() {
		check(stacks.get().peek());
	}

	private void check(Entry entry) {
		foreach(entry, h -> h.check(entry.type, entry.section));
	}

	private void foreach(Entry entry, Consumer<SectionHandler> consumer) {
		getHandlers(entry.type).forEach(consumer);
		defaultHandlers.forEach(consumer);
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
			handlers.putIfAbsent(type, ConcurrentHashMap.newKeySet());
			handlers.get(type).add(handler);
		} else {
			defaultHandlers.add(handler);
		}
	}

	/**
	 * @param type    the profiler section type
	 * @param handler the handler
	 */
	public void removeHandler(String type, SectionHandler handler) {
		if (type != null) {
			handlers.getOrDefault(type, Collections.emptySet()).remove(handler);
		} else {
			defaultHandlers.remove(handler);
		}
	}

	/**
	 * @param type the profiler section type
	 *
	 * @return an unmodifiable collection for the handlers
	 */
	public Collection<SectionHandler> getHandlers(String type) {
		return Collections.unmodifiableCollection(
				handlers.getOrDefault(type, Collections.emptySet()));
	}

	private static class Entry {

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
