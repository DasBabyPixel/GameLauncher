package gamelauncher.gles.states;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.gles.annotation.ThreadDependant;
import gamelauncher.gles.gl.GLContext;
import gamelauncher.gles.gl.GLES32;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StateRegistry {

    private static final Logger logger = Logger.logger();
    private static final ThreadLocal<GLContext> currentContext = new ThreadLocal<>();
    private static final ThreadLocal<GLContext> storage = new ThreadLocal<>();
    private static final Set<GLContext> contexts = ConcurrentHashMap.newKeySet();
    private static final Key CONTEXT = new Key("state_registry_context");

    public static void addContext(GLContext context) {
        StateRegistry.contexts.add(context);
    }

    @ThreadDependant public static void removeContext(GLContext context) throws GameException {
        ContextDependant cd = context(context);
        if (cd != null) cd.cleanup();
        StateRegistry.contexts.remove(context);
    }

    public static ContextDependant context(GLContext context) {
        if (context == null) return null;
        storage.set(context);
        ContextDependant dep = context.storedValue(CONTEXT, () -> new ContextDependant(storage.get()));
        storage.remove();
        return dep;
    }

    public static GLES32 currentGl() {
        return currentContext().gl();
    }

    public static ContextDependant currentContext() {
//        if (StateRegistry.contextByThread.containsKey(Thread.currentThread())) {
//            long c = StateRegistry.contextByThread.get(Thread.currentThread());
//            return StateRegistry.contexts.get(c);
//        }
        return context(currentContext.get());
    }

    @ApiStatus.Internal public static void currentContext(GLContext context) {
        currentContext.set(context);
    }

//	public static void setContextHoldingThread(long id, Thread thread) {
//		StateRegistry.logger.debugf("OpenGL Context %s on Thread %s", id,
//				thread == null ? "null" : thread.getName());
//		if (thread == null) {
//			GLFW.glfwMakeContextCurrent(0L);
//			GLES.setCapabilities(null);
//			Thread th = StateRegistry.contextHoldingThreads.remove(id);
//			if (th != null) {
//				StateRegistry.contextByThread.remove(th);
//			}
//			return;
//		}
//
//		GLFW.glfwMakeContextCurrent(id);
//		GLES.createCapabilities();
//		StateRegistry.contextHoldingThreads.put(id, thread);
//		StateRegistry.contextByThread.put(thread, id);
//	}
}
