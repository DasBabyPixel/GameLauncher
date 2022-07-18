package gamelauncher.engine.util.concurrent;

import java.io.PrintWriter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.engine.util.logging.SelectiveStream.Output;

/**
 * @author DasBabyPixel
 * @param <T>
 */
@SuppressWarnings("javadoc")
public abstract class AbstractQueueSubmissionThread<T> extends Thread {

	protected final ConcurrentLinkedDeque<QueueEntry<T>> queue = new ConcurrentLinkedDeque<>();
	protected final Lock lock;
	protected final Condition condition;
	protected final CountDownLatch exit = new CountDownLatch(1);
	private final CompletableFuture<Void> exitFuture = new CompletableFuture<>();
	private boolean work = false;

	public AbstractQueueSubmissionThread() {
		this.lock = new ReentrantLock(true);
		this.condition = this.lock.newCondition();
	}

	public CompletableFuture<Void> exitFuture() {
		return exitFuture;
	}

	public CompletableFuture<Void> exit() {
		System.out.println("Exit");
		exit.countDown();
		signal();
		return exitFuture();
	}

	protected void startExecuting() {
	}

	protected void stopExecuting() {
	}

	protected void workExecution() {
	}

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
			try {
				loop();
			} catch (Throwable ex) {
				ex.printStackTrace(new PrintWriter(Logger.system.computeOutputStream(Output.ERR)));
			}
		}
		stopExecuting();
		exitFuture.complete(null);
	}

	protected abstract void handleElement(T element) throws GameException;

	public final void workQueue() {
		QueueEntry<T> e;
		while ((e = queue.pollFirst()) != null) {
			if (!shouldHandle(e)) {
				queue.offerFirst(e);
				return;
			}
			try {
				handleElement(e.val);
				e.fut.complete(null);
			} catch (GameException ex) {
				e.fut.completeExceptionally(ex);
				ex.printStackTrace();
			}
		}
	}

	protected boolean shouldHandle(QueueEntry<T> entry) {
		return true;
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

	public final CompletableFuture<Void> submitLast(T element) {
		CompletableFuture<Void> fut = new CompletableFuture<>();
		queue.offerLast(new QueueEntry<T>(fut, element));
		signal();
		return fut;
	}

	public final CompletableFuture<Void> submitFirst(T element) {
		CompletableFuture<Void> fut = new CompletableFuture<>();
		queue.offerFirst(new QueueEntry<T>(fut, element));
		signal();
		return fut;
	}

	public final CompletableFuture<Void> submit(T element) {
		return submitLast(element);
	}

	protected static final class QueueEntry<T> {
		public final CompletableFuture<Void> fut;
		public final T val;

		public QueueEntry(CompletableFuture<Void> fut, T val) {
			this.fut = fut;
			this.val = val;
		}
	}
}
