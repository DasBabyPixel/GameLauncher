package gamelauncher.lwjgl.render.states;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("javadoc")
public class StateRegistry {

	private static final Collection<Long> glfwWindows = new CopyOnWriteArrayList<>();
	private static final Map<Long, Thread> contextHoldingThreads = new ConcurrentHashMap<>();
	private static final Map<Thread, Long> contextByThread = new ConcurrentHashMap<>();

	public static void addWindow(long id) {
		glfwWindows.add(id);
		setContextHoldingThread(id, null);
	}

	public static Thread getContextHolder(long id) {
		return contextHoldingThreads.get(id);
	}

	public static long getHeldContext(Thread thread) {
		return contextByThread.get(thread);
	}

	public static void setContextHoldingThread(long id, Thread thread) {
		System.out.printf("%s: %s%n", id, thread==null?"none":thread.getName());
		if (thread == null) {
			Thread th = contextHoldingThreads.remove(id);
			contextByThread.remove(th);
			return;
		}
		contextHoldingThreads.put(id, thread);
		contextByThread.put(thread, id);
	}

	public static void removeWindow(long id) {
		glfwWindows.remove(id);
		Thread thread = contextHoldingThreads.remove(id);
		if (thread != null) {
			contextByThread.remove(thread);
		}
	}
}
