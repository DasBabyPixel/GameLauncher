package gamelauncher.lwjgl.render.texture;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameResource;
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.lwjgl.render.glfw.GLFWSecondaryContext;

@SuppressWarnings("javadoc")
public class LWJGLAsyncTextureUploader extends Thread implements GameResource, ExecutorThread {

	private final AtomicBoolean exit = new AtomicBoolean(false);
	private final Queue<Future> queue = new ConcurrentLinkedDeque<>();
	private final Lock lock = new ReentrantLock(true);
	private final Condition condition = lock.newCondition();
	final LWJGLTextureManager manager;
	final GLFWSecondaryContext secondaryContext;

	public LWJGLAsyncTextureUploader(LWJGLTextureManager manager) {
		this.manager = manager;
		this.secondaryContext = Threads.waitFor(manager.launcher.getWindow().createSecondaryContext());
		setName("GL-AsyncTextureUploader");
		start();
	}

	@Override
	public void run() {
		secondaryContext.makeCurrent();
		while (!exit.get()) {
			lock.lock();
			while (!queue.isEmpty()) {
				workQueue();
			}
			try {
				condition.await();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
			lock.unlock();
			workQueue();
		}
		secondaryContext.destroyCurrent();
	}

	@Override
	public void workQueue() {
		if (queue.isEmpty()) {
			return;
		}
		Future f;
		while ((f = queue.poll()) != null) {
			try {
				f.run.run();
				f.fut.complete(null);
			} catch (GameException ex) {
				f.fut.completeExceptionally(ex);
				manager.launcher.handleError(ex);
			}
		}
	}

	@Override
	public void cleanup() throws GameException {
		this.secondaryContext.cleanup();
		this.exit.set(true);
		lock.lock();
		condition.signal();
		lock.unlock();
	}

	@Override
	public CompletableFuture<Void> submit(GameRunnable runnable) {
		CompletableFuture<Void> fut = new CompletableFuture<>();
		lock.lock();
		queue.offer(new Future(fut, runnable));
		condition.signal();
		lock.unlock();
		return fut;
	}

	private static class Future {
		private final CompletableFuture<Void> fut;
		private final GameRunnable run;

		public Future(CompletableFuture<Void> fut, GameRunnable run) {
			this.fut = fut;
			this.run = run;
		}
	}
}
