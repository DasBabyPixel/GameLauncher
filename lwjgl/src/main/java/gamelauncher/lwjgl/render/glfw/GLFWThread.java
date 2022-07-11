package gamelauncher.lwjgl.render.glfw;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

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
			glfwWaitEventsTimeout(1.0D);
			glfwPollEvents();
			workQueue();
		}
		while (!users.isEmpty()) {
			for (GLFWUser user : users) {
				CompletableFuture<Void> fut = user.doneFuture();
				if (fut.isDone()) {
					users.remove(user);
					continue;
				}
				try {
					user.destroy();
					fut.get(5, TimeUnit.SECONDS);
					users.remove(user);
				} catch (InterruptedException | ExecutionException ex) {
					launcher.handleError(ex);
				} catch (TimeoutException ex) {
					logger.errorf("GLFW-Thread user took more than 5 seconds to finish. This mustn't happen!");
				}
			}
		}
		glfwTerminate();
	}

	@Override
	public CompletableFuture<Void> submit(GameRunnable runnable) {
		CompletableFuture<Void> fut = new CompletableFuture<>();
		queue.offer(new Entry(fut, runnable));
		return fut;
	}

	public CompletableFuture<Void> terminate() {
		terminate.set(true);
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
