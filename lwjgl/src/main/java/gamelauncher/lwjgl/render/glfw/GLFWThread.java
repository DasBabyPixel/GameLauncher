package gamelauncher.lwjgl.render.glfw;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.LWJGLGameLauncher;

@SuppressWarnings("javadoc")
public class GLFWThread extends Thread implements ExecutorThread {

	private final Queue<Entry> queue = new ConcurrentLinkedDeque<>();
	private final LWJGLGameLauncher launcher;
	private final AtomicBoolean terminate = new AtomicBoolean(false);
	private final CompletableFuture<Void> terminateFuture = new CompletableFuture<>();
	private final Collection<GLFWUser> users = ConcurrentHashMap.newKeySet();
	private final Lock lock = new ReentrantLock(true);
	private final Condition condition = lock.newCondition();
	private final Logger logger = Logger.getLogger();

	public GLFWThread(LWJGLGameLauncher launcher) {
		this.launcher = launcher;
		this.setName("GLFW-Thread");
	}

	@Override
	public void run() {
		if (!glfwInit()) {
			throw new ExceptionInInitializerError("Couldn't initialize GLFW");
		}
		while (!terminate.get()) {
			glfwWaitEventsTimeout(0.5D);
			glfwPollEvents();
			workQueue();
		}
		for (GLFWUser user : users) {
			user.destroy();
		}
		while (true) {
			workQueue();
			if (users.isEmpty()) {
				break;
			}
			lock.lock();
			try {
				condition.await();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
			lock.unlock();
		}
		if (!users.isEmpty()) {
			logger.errorf("Not all users of the GLFWThread have been cleared: %n%s", users);
		}
		glfwTerminate();
		this.terminateFuture.complete(null);
	}

	private void signal() {
		if (!terminate.get()) {
			glfwPostEmptyEvent();
		} else {
			lock.lock();
			condition.signal();
			lock.unlock();
		}
	}

	@Override
	public CompletableFuture<Void> submit(GameRunnable runnable) {
		CompletableFuture<Void> fut = new CompletableFuture<>();
		queue.offer(new Entry(fut, runnable));
		signal();
		return fut;
	}

	void addUser(GLFWUser user) {
		users.add(user);
		signal();
	}

	void removeUser(GLFWUser user) {
		users.remove(user);
		signal();
	}

	public CompletableFuture<Void> terminate() {
		terminate.set(true);
		signal();
		return terminateFuture;
	}

	@Override
	public void workQueue() {
		Entry e;
		while (!queue.isEmpty()) {
			e = queue.poll();
			try {
				e.runnable.run();
				e.future.complete(null);
			} catch (GameException ex) {
				e.future.completeExceptionally(ex);
				launcher.handleError(ex);
			}
		}
	}

	private static class Entry {
		private final CompletableFuture<Void> future;
		private final GameRunnable runnable;

		public Entry(CompletableFuture<Void> future, GameRunnable runnable) {
			this.future = future;
			this.runnable = runnable;
		}

	}
}
