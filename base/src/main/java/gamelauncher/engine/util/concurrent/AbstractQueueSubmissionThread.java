package gamelauncher.engine.util.concurrent;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.engine.util.logging.SelectiveStream.Output;

import java.io.PrintWriter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @param <T>
 * @author DasBabyPixel
 */
public abstract class AbstractQueueSubmissionThread<T> extends AbstractGameThread {
    private static final Logger logger = Logger.logger();
    protected final ConcurrentLinkedDeque<QueueEntry<T>> queue = new ConcurrentLinkedDeque<>();
    protected final AtomicInteger size = new AtomicInteger();
    protected final Phaser sizeLocker = new Phaser(1);
    private final CompletableFuture<Void> exitFuture = new CompletableFuture<>();
    private final AtomicBoolean work = new AtomicBoolean(false);
    protected volatile boolean exit = false;

    /**
     *
     */
    public AbstractQueueSubmissionThread(GameLauncher launcher) {
        super(launcher);
    }

    /**
     * @return the exitfuture
     */
    public CompletableFuture<Void> exitFuture() {
        return this.exitFuture;
    }

    /**
     * @return the exitfuture
     */
    public CompletableFuture<Void> exit() {
        this.exit = true;
        this.signal();
        return this.exitFuture();
    }

    protected void startExecuting() {
        logger.debug("Starting " + getName() + " [" + getClass().getSimpleName() + "]");
    }

    protected void stopExecuting() {
    }

    protected void workExecution() {
    }

    protected void loop() {
        this.waitForSignal();
        this.workQueue();
        this.workExecution();
    }

    protected void waitForSignal() {
        if (this.exit) return;
        while (!this.work.compareAndSet(true, false)) {
            Threads.park();
        }
    }

    @Override
    public final void run() {
        this.startExecuting();
        while (!this.exit) {
            try {
                this.loop();
            } catch (Throwable ex) {
                ex.printStackTrace(new PrintWriter(Logger.system.computeOutputStream(Output.ERR)));
            }
        }
        this.loop();
        this.stopExecuting();
        this.loop();
        this.exitFuture.complete(null);
    }

    protected abstract void handleElement(T element) throws GameException;

    /**
     * Works off all the elements in the queue
     */
    public final void workQueue() {
        QueueEntry<T> e;
        while ((e = this.queue.pollFirst()) != null) {
            if (!this.shouldHandle(e)) {
                this.queue.offerFirst(e);
                return;
            }
            int nsize = size.decrementAndGet();
            if (nsize < 600) {
                sizeLocker.arrive();
            }
            try {
                this.handleElement(e.val);
                e.fut.complete(null);
            } catch (Throwable ex) {
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
        if (this.work.compareAndSet(false, true)) {
            Threads.unpark(this);
        }
    }

    /**
     * @param element the element
     * @return a new future for the submitted element
     */
    public final CompletableFuture<Void> submitLast(T element) {
        CompletableFuture<Void> fut = new CompletableFuture<>();
        do {
            int phase = sizeLocker.getPhase();
            if (size.get() > 1000) {
                sizeLocker.awaitAdvance(phase);
            }
        } while (size.get() > 1000);
        this.queue.offerLast(new QueueEntry<>(fut, element));
        size.incrementAndGet();
        this.signal();
        return fut;
    }

    /**
     * @param element the element
     * @return a new future for the submitted element
     */
    public final CompletableFuture<Void> submitFirst(T element) {
        CompletableFuture<Void> fut = new CompletableFuture<>();
        do {
            int phase = sizeLocker.getPhase();
            if (size.get() > 1000) {
                sizeLocker.awaitAdvance(phase);
            }
        } while (size.get() > 1000);
        this.queue.offerFirst(new QueueEntry<T>(fut, element));
        size.incrementAndGet();
        this.signal();
        return fut;
    }

    /**
     * @param element the element
     * @return a new future from the submitted element
     */
    public final CompletableFuture<Void> submit(T element) {
        return this.submitLast(element);
    }

    protected static class QueueEntry<T> {
        private final CompletableFuture<Void> fut;
        private final T val;

        public QueueEntry(CompletableFuture<Void> fut, T val) {
            this.fut = fut;
            this.val = val;
        }

        public CompletableFuture<Void> fut() {
            return fut;
        }

        public T val() {
            return val;
        }
    }

}
