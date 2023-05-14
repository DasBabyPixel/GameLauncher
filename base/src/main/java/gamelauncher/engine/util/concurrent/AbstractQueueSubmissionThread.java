package gamelauncher.engine.util.concurrent;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventPoller;
import com.lmax.disruptor.RingBuffer;
import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.engine.util.logging.SelectiveStream.Output;
import java8.util.concurrent.CompletableFuture;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author DasBabyPixel
 */
public abstract class AbstractQueueSubmissionThread<T> extends AbstractGameThread {
    protected final RingBuffer<QueueEntry<T>> queue;
    protected final EventPoller<QueueEntry<T>> poller;
    private final Logger logger = Logger.logger(getClass());
    private final CompletableFuture<Void> exitFuture = new CompletableFuture<>();
    private final AtomicBoolean work = new AtomicBoolean(false);
    protected volatile boolean exit = false;

    @Api public AbstractQueueSubmissionThread(GameLauncher launcher) {
        super(launcher);
        this.queue = RingBuffer.createMultiProducer(QueueEntry::new, 1024, new BlockingWaitStrategy());
        this.poller = queue.newPoller();
        queue.addGatingSequences(poller.getSequence());
    }

    /**
     * @return the exitfuture
     */
    @Api public final CompletableFuture<Void> exitFuture() {
        return this.exitFuture;
    }

    /**
     * @return the exitfuture
     */
    @Api public final CompletableFuture<Void> exit() {
        this.exit = true;
        this.signal();
        return this.exitFuture();
    }

    @Api protected void startExecuting() {
        logger.debug("Starting " + getName() + " [" + getClass().getSimpleName() + "]");
    }

    @Api protected void stopExecuting() {
    }

    @Api protected void workExecution() {
    }

    @Api protected final void loop() {
        this.waitForSignal();
        this.workQueue();
        this.workExecution();
    }

    @Api protected final void waitForSignal() {
        if (this.exit) return;
        while (!this.work.compareAndSet(true, false)) {
            Threads.park();
        }
    }

    @Override public final void run() {
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
        stopTracking();
        this.exitFuture.complete(null);
    }

    @Api protected abstract void handleElement(T element) throws GameException;

    /**
     * Handles all the elements in the queue
     */
    @Api public final void workQueue() {
        try {
            poller.poll((event, sequence, endOfBatch) -> {
                try {
                    this.handleElement(event.val);
                    event.fut.complete(null);
                } catch (Throwable t) {
                    event.fut.completeExceptionally(t);
                    OutputStream out = Logger.system.computeOutputStream(Output.ERR);
                    t.printStackTrace(new PrintStream(out, true));
                }
                return true;
            });
        } catch (Exception ex) {
            ex.printStackTrace(new PrintStream(Logger.system.computeOutputStream(Output.ERR), true));
        }
    }

    @Api protected boolean shouldWaitForSignal() {
        return true;
    }

    @Api protected void signal() {
        if (this.work.compareAndSet(false, true)) {
            Threads.unpark(this);
        }
    }

    /**
     * @param element the element
     * @return a new future from the submitted element
     */
    @Api public final CompletableFuture<Void> submit(T element) {
        CompletableFuture<Void> fut = new CompletableFuture<>();
        this.queue.publishEvent((event, sequence, arg0, arg1) -> {
            event.fut = arg1;
            event.val = arg0;
        }, element, fut);
        signal();
        return fut;
    }

    @Api
    private static class QueueEntry<T> {
        private CompletableFuture<Void> fut;
        private T val;

        @Api public CompletableFuture<Void> fut() {
            return fut;
        }

        @Api public T val() {
            return val;
        }
    }

}
