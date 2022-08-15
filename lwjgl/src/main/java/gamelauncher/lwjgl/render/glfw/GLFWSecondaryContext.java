package gamelauncher.lwjgl.render.glfw;

import static org.lwjgl.glfw.GLFW.*;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameResource;
import gamelauncher.lwjgl.render.states.StateRegistry;

@SuppressWarnings("javadoc")
public class GLFWSecondaryContext implements GameResource {

	private final Lock lock = new ReentrantLock(true);
	private long id;
	private final GLFWWindow window;
	private final AtomicReference<Thread> current = new AtomicReference<>();

	public GLFWSecondaryContext(GLFWWindow window) {
		this.window = window;
	}

	void create() {
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_ES_API);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		lock.lock();
		id = glfwCreateWindow(1, 1, "unused", 0, window.getGLFWId());
		StateRegistry.addWindow(id);
		lock.unlock();
	}

	public boolean isCurrent() {
		return Thread.currentThread() == current.get();
	}

	public void makeCurrent() {
		lock.lock();
		current.set(Thread.currentThread());
		StateRegistry.setContextHoldingThread(id, Thread.currentThread());
		lock.unlock();
	}

	public void destroyCurrent() {
		lock.lock();
		current.set(null);
		StateRegistry.setContextHoldingThread(id, null);
		lock.unlock();
	}

	@Override
	public void cleanup() throws GameException {
		lock.lock();
		StateRegistry.removeWindow(id);
		glfwDestroyWindow(id);
		lock.unlock();
	}
}
