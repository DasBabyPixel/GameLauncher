package gamelauncher.engine.util.concurrent;

import java.io.PrintWriter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.engine.util.logging.SelectiveStream.Output;

/**
 * @author DasBabyPixel
 * @param <T>
 */
@SuppressWarnings("javadoc")
public abstract class AbstractQueueSubmissionThread<T> extends AbstractGameThread {

	protected final ConcurrentLinkedDeque<QueueEntry<T>> queue = new ConcurrentLinkedDeque<>();

	protected volatile boolean exit = false;

	private final CompletableFuture<Void> exitFuture = new CompletableFuture<>();

	private final AtomicBoolean work = new AtomicBoolean(false);

	public AbstractQueueSubmissionThread() {
	}

	public CompletableFuture<Void> exitFuture() {
		return exitFuture;
	}

	public CompletableFuture<Void> exit() {
		exit = true;
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
		if (exit)
			return;
		while (!work.compareAndSet(true, false)) {
			Threads.park();
		}
	}

	@Override
	public final void run() {
		startExecuting();
		while (!exit) {
			try {
				loop();
			} catch (Throwable ex) {
				ex.printStackTrace(new PrintWriter(Logger.system.computeOutputStream(Output.ERR)));
			}
		}
		loop();
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

	protected boolean shouldWaitForSignal() {
		return true;
	}

	protected void signal() {
		if (work.compareAndSet(false, true)) {
			Threads.unpark(this);
		}
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
