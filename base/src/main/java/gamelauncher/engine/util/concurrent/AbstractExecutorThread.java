package gamelauncher.engine.util.concurrent;

import java.util.Deque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.engine.util.logging.Logger;

@SuppressWarnings("javadoc")
public abstract class AbstractExecutorThread extends Thread implements ExecutorThread {

	private static final Logger logger = Logger.getLogger(AbstractExecutorThread.class);

	private final Deque<QueueEntry> queue = new ConcurrentLinkedDeque<>();

	protected final Lock lock;

	protected final Condition condition;

	protected final CountDownLatch exit = new CountDownLatch(1);

	private final CompletableFuture<Void> exitFuture = new CompletableFuture<>();

	private boolean work = false;

	public WrapperEntry currentEntry;

	public AbstractExecutorThread(ThreadGroup group) {
		super(group, (Runnable) null);
		this.lock = new ReentrantLock(true);
		this.condition = this.lock.newCondition();
	}

	public CompletableFuture<Void> exitFuture() {
		return exitFuture;
	}

	public CompletableFuture<Void> exit() {
		exit.countDown();
		signal();
		return exitFuture();
	}

	protected abstract void startExecuting();

	protected abstract void stopExecuting();

	protected abstract void workExecution();

	protected void loop() {
		waitForSignal();
		workQueue();
		workExecution();
	}

	protected void waitForSignal() {
		this.lock.lock();
		if (!work) {
			if (useCondition()) {
				this.condition.awaitUninterruptibly();
			}
		}
		work = false;
		this.lock.unlock();
	}

	@Override
	public final void run() {
		startExecuting();
		while (exit.getCount() != 0L) {
			loop();
		}
		stopExecuting();
		exitFuture.complete(null);
	}

	@Override
	public final void workQueue() {
		QueueEntry e;
		while ((e = queue.pollFirst()) != null) {
			if (!shouldHandle(e)) {
				queue.offerFirst(e);
				return;
			}
			if (Threads.calculateThreadStacks) {
				currentEntry = e.entry;
			}
			work(e.run, e.fut);
			if (Threads.calculateThreadStacks) {
				currentEntry = null;
			}
		}
	}

	private void work(GameRunnable run, CompletableFuture<Void> fut) {
		try {
			run.run();
			fut.complete(null);
		} catch (GameException ex) {
			String msg = ex.getLocalizedMessage();
			GameException ex2 = new GameException("Exception in ExecutorThread" + (msg == null ? "" : (": " + msg)));
			ex2.initCause(ex);

			if (currentEntry != null) {
				Throwable t = currentEntry.calculateCause();
				if (t != null) {
					ex2.addSuppressed(t);
				}
			}
			logger.error(ex2);
			fut.completeExceptionally(ex2);
		}
	}

	protected boolean shouldHandle(QueueEntry entry) {
		return true;
	}

	@Override
	public Thread thread() {
		return this;
	}

	protected boolean useCondition() {
		return true;
	}

	protected void signal() {
		this.lock.lock();
		work = true;
		this.condition.signal();
		this.lock.unlock();
	}

	@Override
	public final CompletableFuture<Void> submitLast(GameRunnable runnable) {
		CompletableFuture<Void> fut = new CompletableFuture<>();
		if (Thread.currentThread() == this) {
			work(runnable, fut);
		} else {
			queue.offerLast(new QueueEntry(fut, runnable, WrapperEntry.newEntry()));
			signal();
		}
		return fut;
	}

	@Override
	public final CompletableFuture<Void> submitFirst(GameRunnable runnable) {
		CompletableFuture<Void> fut = new CompletableFuture<>();
		if (Thread.currentThread() == this) {
			work(runnable, fut);
		} else {
			queue.offerFirst(new QueueEntry(fut, runnable, WrapperEntry.newEntry()));
			signal();
		}
		return fut;
	}

	protected static final class QueueEntry {

		public final WrapperEntry entry;

		public final CompletableFuture<Void> fut;

		public final GameRunnable run;

		public QueueEntry(CompletableFuture<Void> fut, GameRunnable run, WrapperEntry entry) {
			this.fut = fut;
			this.run = run;
			this.entry = entry;
		}

	}

}
