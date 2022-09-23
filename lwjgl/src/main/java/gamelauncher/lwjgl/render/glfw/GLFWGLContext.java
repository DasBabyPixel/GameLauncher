package gamelauncher.lwjgl.render.glfw;

import static org.lwjgl.glfw.GLFW.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.render.states.StateRegistry;

@SuppressWarnings("javadoc")
public class GLFWGLContext extends AbstractGameResource {

	private final Lock lock = new ReentrantLock(true);
	
	private long id;

	private final GLFWWindow window;
	
	private final CountDownLatch latch = new CountDownLatch(1);

	private final AtomicReference<Thread> current = new AtomicReference<>();

	public GLFWGLContext(GLFWWindow window) {
		this.window = window;
	}

	void create() {
		glfwDefaultWindowHints();
		applyHints();
		try {
			lock.lock();
			id = glfwCreateWindow(1, 1, "unused", 0, window.getGLFWId());
			StateRegistry.addWindow(id);
		} finally {
			lock.unlock();
			latch.countDown();
		}
	}

	public void makeCurrent() {
		try {
			latch.await();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		lock.lock();
		current.set(Thread.currentThread());
		StateRegistry.setContextHoldingThread(id, Thread.currentThread());
		lock.unlock();
	}

	public void destroyCurrent() {
		try {
			latch.await();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		lock.lock();
		current.set(null);
		StateRegistry.setContextHoldingThread(id, null);
		lock.unlock();
	}

	@Override
	public void cleanup0() throws GameException {
		try {
			latch.await();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		window.removeContext(this);
		lock.lock();
		StateRegistry.removeWindow(id);
		glfwDestroyWindow(id);
		lock.unlock();
	}

	protected void applyHints() {
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_ES_API);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

		glfwWindowHint(GLFW_CONTEXT_DEBUG, GLFW_TRUE);
		glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
	}

	public boolean isCurrent() {
		return Thread.currentThread() == current.get();
	}

	public long getGLFWId() {
		try {
			try {
				latch.await();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
			lock.lock();
			return id;
		} finally {
			lock.unlock();
		}
	}

}
