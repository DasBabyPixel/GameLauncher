package gamelauncher.engine.util.concurrent;

import java.util.Deque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.engine.util.logging.Logger;

@SuppressWarnings("javadoc")
public abstract class AbstractExecutorThread extends AbstractGameThread implements ExecutorThread {

	private static final Logger logger = Logger.getLogger(AbstractExecutorThread.class);

	private final Deque<QueueEntry> queue = new ConcurrentLinkedDeque<>();

	protected volatile boolean exit = false;

	private final CompletableFuture<Void> exitFuture = new CompletableFuture<>();

	private final AtomicBoolean work = new AtomicBoolean();

	private final AtomicBoolean parked = new AtomicBoolean(false);

	private final AtomicBoolean unpark = new AtomicBoolean(false);

	public WrapperEntry currentEntry;

	private boolean skipNextSignalWait = false;

	public AbstractExecutorThread(ThreadGroup group) {
		super(group, (Runnable) null);
	}

	public CompletableFuture<Void> exitFuture() {
		return exitFuture;
	}

	public CompletableFuture<Void> exit() {
		exit = true;
		signal();
		return exitFuture();
	}

	@Override
	protected void cleanup0() throws GameException {
		Threads.waitFor(exit());
	}

	@Override
	public void park() {
		if (Thread.currentThread() != this) {
			throw new SecurityException("May not call this from any other thread than self");
		}
		if (unpark.compareAndSet(true, false)) {
			return;
		}
		parked.set(true);
		if (unpark.compareAndSet(true, false)) {
			parked.set(false);
			return;
		}
		LockSupport.park();
	}

	@Override
	public void park(long nanos) {
		if (Thread.currentThread() != this) {
			throw new SecurityException("May not call this from any other thread than self");
		}
		if (unpark.compareAndSet(true, false)) {
			return;
		}
		parked.set(true);
		if (unpark.compareAndSet(true, false)) {
			parked.set(false);
			return;
		}
		LockSupport.parkNanos(nanos);
	}

	@Override
	public void unpark() {
		if (parked.compareAndSet(true, false)) {
			LockSupport.unpark(this);
		} else {
			unpark.set(true);
			if (parked.compareAndSet(true, false)) {
				unpark.set(false);
				LockSupport.unpark(this);
			}
		}
	}

	protected abstract void startExecuting();

	protected abstract void stopExecuting();

	protected abstract void workExecution();

	protected void loop() {
		if (shouldWaitForSignal()) {
			waitForSignal();
		}
		workQueue();
		workExecution();
	}

	protected void waitForSignal() {
		if (exit)
			return;
		if (skipNextSignalWait) {
			skipNextSignalWait = false;
			return;
		}
		while (!work.compareAndSet(true, false)) {
			Threads.park();
		}
	}

	protected void waitForSignalTimeout(long nanos) {
		if (exit)
			return;
		if (skipNextSignalWait) {
			skipNextSignalWait = false;
			return;
		}
		final long begin = System.nanoTime();
		skipNextSignalWait = true;
		while (!work.compareAndSet(true, false)) {
			long parktime = begin + nanos - System.nanoTime();
			if (parktime < 0) {
				skipNextSignalWait = false;
				break;
			}
			Threads.park(parktime);
		}
	}

	@Override
	public final void run() {
		startExecuting();
		while (!exit) {
			loop();
		}
		loop();
		stopExecuting();
		exitFuture.complete(null);
		if (!cleanedUp) {
			cleanedUp = true;
			AbstractGameResource.logCleanup(this);
		}
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
			GameException ex2 = buildStacktrace();
			ex2.initCause(ex);
			logger.error(ex2);
			fut.completeExceptionally(ex2);
		}
	}

	public GameException buildStacktrace() {
		GameException ex = new GameException("Exception in ExecutorThread");
		if (currentEntry != null) {
			Throwable t = currentEntry.calculateCause();
			if (t != null) {
				ex.addSuppressed(t);
			}
		}
		return ex;
	}

	protected boolean shouldHandle(QueueEntry entry) {
		return true;
	}

	@Override
	public Thread thread() {
		return this;
	}

	protected boolean shouldWaitForSignal() {
		return true;
	}

	protected void signal() {
		if (work.compareAndSet(false, true)) {
			Threads.unpark((ParkableThread) this);
		}
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
