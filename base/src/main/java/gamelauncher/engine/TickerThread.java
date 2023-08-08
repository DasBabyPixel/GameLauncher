package gamelauncher.engine;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.AbstractGameThread;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameCallable;
import gamelauncher.engine.util.function.GameRunnable;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import java8.util.concurrent.CompletableFuture;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author DasBabyPixel
 */
public class TickerThread extends AbstractGameThread implements ExecutorThread {

    private final AtomicBoolean exit = new AtomicBoolean(false);
    private final CompletableFuture<Void> exitFuture = new CompletableFuture<>();
    private final Queue<GameRunnable> queue = new ConcurrentLinkedQueue<>();
    private final LongArrayFIFOQueue ticks = new LongArrayFIFOQueue();
    private final LongArrayFIFOQueue tickTimes = new LongArrayFIFOQueue();
    private final GameLauncher launcher;
    private final AtomicInteger tick = new AtomicInteger(0);
    private final long second = TimeUnit.SECONDS.toNanos(1);
    private final long tickTime = (long) (second / GameLauncher.MAX_TPS);
    private final AtomicLong lastTick = new AtomicLong(-1L);
    private long ticksTimeSum = 0;
    private long ticksTimeSumCount = 0;

    public TickerThread(GameLauncher launcher) {
        super(launcher);
        this.launcher = launcher;
        this.setName("GameThread");
        this.setPriority(MAX_PRIORITY);

    }

    /**
     * Computes the partial tick
     *
     * @return the current partial tick
     */
    @Api public float partialTick() {
        long lastTick = this.lastTick.get();
        if (lastTick == -1) {
            return 0;
        }
        long nanos = System.nanoTime();
        long diff = nanos - lastTick;
        return diff / (float) tickTime;
    }

    @Override public void run() {
        // t1 - t2 < 0 | t1 < t2
        workQueue();
        long lastTick = System.nanoTime();
        long lastWarning = System.nanoTime();

        do {
            long nanoDelay = lastTick - System.nanoTime();
            if (nanoDelay > 0) Threads.park(nanoDelay);

            long nextTick = lastTick + tickTime;
            long diff = System.nanoTime() - nextTick;
            if (diff > 2_000_000_000L /* 2 seconds */ && nextTick - lastWarning > 5_000_000_000L /* 5 Seconds */) {
                long skip = diff / tickTime;
                launcher.logger().warnf("Can't keep up! Overloaded? Skipping %s ticks (%sms)", skip, TimeUnit.NANOSECONDS.toMillis(diff));
                lastWarning = System.nanoTime();
                nextTick += (skip - 1) * tickTime;
            }

            long tickStart = System.nanoTime();
            tick(System.nanoTime());
            long tickStop = System.nanoTime();
            long tickTook = tickStop - tickStart;
            tickTimes.enqueue(tickTook);
            ticksTimeSum += tickTook;
            ticksTimeSumCount++;
            lastTick = nextTick;

        } while (!exit.get());
        workQueue();

        exitFuture.complete(null);
    }

    /**
     * @return the current tps
     */
    @Api public int tps() {
        removeOldTicks();
        return ticks.size();
    }

    /**
     * @return the average tick time over the last second
     */
    @Api public double averageTickTime() {
        return (double) ticksTimeSum / (double) ticksTimeSumCount;
    }

    @Override public <T> CompletableFuture<T> submit(GameCallable<T> callable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        if (Threads.currentThread() == this) {
            try {
                future.complete(callable.call());
            } catch (Throwable e) {
                future.completeExceptionally(e);
            }
            return future;
        }
        queue.offer(() -> {
            try {
                T t = callable.call();
                future.complete(t);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    @Override public CompletableFuture<Void> submit(GameRunnable runnable) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        if (Threads.currentThread() == this) {
            try {
                runnable.run();
                future.complete(null);
            } catch (Throwable e) {
                future.completeExceptionally(e);
            }
            return future;
        }
        queue.offer(() -> {
            try {
                runnable.run();
                future.complete(null);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    @Override public String name() {
        return getName();
    }

    /**
     * @return the current tick
     */
    @Api public int currentTick() {
        return tick.get();
    }

    @Override public void workQueue() {
        GameRunnable runnable;
        while ((runnable = queue.poll()) != null) {
            try {
                runnable.run();
            } catch (GameException ex) {
                launcher.handleError(ex);
            }
        }
    }

    @Override protected CompletableFuture<Void> cleanup0() throws GameException {
        exit.set(true);
        return exitFuture;
    }

    protected void launcherTick() {
        try {
            launcher.tick();
        } catch (Throwable ex) {
            launcher.handleError(ex);
        }
    }

    private void removeOldTicks() {
        long compareTo = System.nanoTime() - second + tickTime;
        while (!tickTimes.isEmpty()) {
            long first = ticks.firstLong();
            if (compareTo - first > 0) {
                ticks.dequeueLong();
                long took = tickTimes.dequeueLong();
                ticksTimeSum -= took;
                ticksTimeSumCount--;
                continue;
            }
            break;
        }
    }

    private void offerTick(long nanos) {
        removeOldTicks();
        this.lastTick.set(nanos - tickTime);
        ticks.enqueue(nanos);
    }

    private void tick(long tickTime) {
        offerTick(tickTime);
        tick.incrementAndGet();
        launcherTick();
        workQueue();
    }
}
