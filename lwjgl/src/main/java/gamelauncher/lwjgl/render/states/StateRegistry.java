package gamelauncher.lwjgl.render.states;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("javadoc")
public class StateRegistry {

	private static final Collection<Long> glfwWindows = new CopyOnWriteArrayList<>();
	private static final Map<Long, Thread> contextHoldingThreads = new ConcurrentHashMap<>();
	private static final Map<Long, ContextDependant> contexts = new ConcurrentHashMap<>();
	private static final Map<Thread, Long> contextByThread = new ConcurrentHashMap<>();

	public static void addWindow(long id) {
		glfwWindows.add(id);
		contexts.put(id, new ContextDependant(id));
	}

	public static Thread getContextHolder(long id) {
		return contextHoldingThreads.get(id);
	}

	public static long getHeldContext(Thread thread) {
		return contextByThread.get(thread);
	}

	public static ContextDependant currentContext() {
		if (contextByThread.containsKey(Thread.currentThread())) {
			long c = contextByThread.get(Thread.currentThread());
			return contexts.get(c);
		}
		return null;
	}

	public static void setContextHoldingThread(long id, Thread thread) {
		if (thread == null) {
			Thread th = contextHoldingThreads.remove(id);
			if (th != null) {
				contextByThread.remove(th);
			}
			return;
		}
		contextHoldingThreads.put(id, thread);
		contextByThread.put(thread, id);
	}

	public static void removeWindow(long id) {
		glfwWindows.remove(id);
		contexts.remove(id);
		Thread thread = contextHoldingThreads.remove(id);
		if (thread != null) {
			contextByThread.remove(thread);
		}
	}
}
