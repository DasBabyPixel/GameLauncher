package gamelauncher.lwjgl.render.states;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.logging.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengles.GLES;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class StateRegistry {

	private static final Logger logger = Logger.logger();

	private static final Collection<Long> glfwWindows = new CopyOnWriteArrayList<>();

	private static final Map<Long, Thread> contextHoldingThreads = new ConcurrentHashMap<>();

	private static final Map<Long, ContextDependant> contexts = new ConcurrentHashMap<>();

	private static final Map<Thread, Long> contextByThread = new ConcurrentHashMap<>();

	public static void addWindow(long id) {
		StateRegistry.glfwWindows.add(id);
		StateRegistry.contexts.put(id, new ContextDependant(id));
	}

	public static Thread getContextHolder(long id) {
		return StateRegistry.contextHoldingThreads.get(id);
	}

	public static long getHeldContext(Thread thread) {
		return StateRegistry.contextByThread.get(thread);
	}

	public static ContextDependant currentContext() {
		if (StateRegistry.contextByThread.containsKey(Thread.currentThread())) {
			long c = StateRegistry.contextByThread.get(Thread.currentThread());
			return StateRegistry.contexts.get(c);
		}
		return null;
	}

	public static void setContextHoldingThread(long id, Thread thread) {
		StateRegistry.logger.debugf("OpenGL Context %s on Thread %s", id,
				thread == null ? "null" : thread.getName());
		if (thread == null) {
			GLFW.glfwMakeContextCurrent(0L);
			GLES.setCapabilities(null);
			Thread th = StateRegistry.contextHoldingThreads.remove(id);
			if (th != null) {
				StateRegistry.contextByThread.remove(th);
			}
			return;
		}

		GLFW.glfwMakeContextCurrent(id);
		GLES.createCapabilities();
		StateRegistry.contextHoldingThreads.put(id, thread);
		StateRegistry.contextByThread.put(thread, id);
	}

	public static void removeContext(long id) throws GameException {
		ContextDependant cd = StateRegistry.contexts.get(id);
		if (cd != null)
			cd.cleanup();
		StateRegistry.contexts.remove(id);
	}

	public static void removeWindow(long id) throws GameException {
		ContextDependant cd = StateRegistry.contexts.get(id);
		if (cd != null)
			cd.cleanup();
		StateRegistry.contexts.remove(id);
		Thread thread = StateRegistry.contextHoldingThreads.remove(id);
		if (thread != null) {
			StateRegistry.contextByThread.remove(thread);
		}
		StateRegistry.glfwWindows.remove(id);
	}

}
