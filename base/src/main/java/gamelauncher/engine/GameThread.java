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
public class GameThread extends AbstractGameThread implements ExecutorThread {

    private final AtomicBoolean exit = new AtomicBoolean(false);
    private final CompletableFuture<Void> exitFuture = new CompletableFuture<>();
    private final Queue<GameRunnable> queue = new ConcurrentLinkedQueue<>();
    private final LongArrayFIFOQueue ticks = new LongArrayFIFOQueue();
    private final LongArrayFIFOQueue tickTimes = new LongArrayFIFOQueue();
    private final GameLauncher gameLauncher;
    private final AtomicInteger tick = new AtomicInteger(0);
    private final long second = TimeUnit.SECONDS.toNanos(1);
    private final long tickTime = (long) (second / GameLauncher.MAX_TPS);
    private final AtomicLong lastTick = new AtomicLong(-1L);
    private long ticksTimeSum = 0;
    private long ticksTimeSumCount = 0;

    public GameThread(GameLauncher launcher) {
        super(launcher);
        this.gameLauncher = launcher;
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
        long tpsTrySkipCounter = 0;

        mainLoop:
        while (true) {
            long nanoDelay = lastTick - System.nanoTime();
            if (nanoDelay > 0) {
                Threads.park(nanoDelay);
            }
            while (lastTick - System.nanoTime() < 0) {
                int tps = tps();
                if (tps < GameLauncher.MAX_TPS * 0.9) {
                    tpsTrySkipCounter++;
                    if (tpsTrySkipCounter > GameLauncher.MAX_TPS && tpsTrySkipCounter > 1) {
                        gameLauncher.logger().infof("Low TPS!");
                        tpsTrySkipCounter = 0;
                    }
                } else {
                    tpsTrySkipCounter = 0;
                }
                lastTick += tickTime;
                long tickStart = System.nanoTime();
                tick(lastTick);
                long tickStop = System.nanoTime();
                long tickTook = tickStop - tickStart;
                tickTimes.enqueue(tickTook);
                ticksTimeSum += tickTook;
                ticksTimeSumCount++;
                if (tickTook > tickTime) {
                    long tooLong = tickTook - tickTime;
                    long ticksToSkip = tooLong / tickTime;
                    gameLauncher.logger().infof("Tick took %sms. This is %sms longer than expected!%s", TimeUnit.NANOSECONDS.toMillis(tickTook), TimeUnit.NANOSECONDS.toMillis(tooLong), ticksToSkip > 0 ? String.format("%nSkipping %s ticks to compensate", ticksToSkip) : "");
                    if (ticksToSkip > 0) {
                        lastTick += (ticksToSkip - 1) * tickTime;
                    }
                }
                if (exit.get()) break mainLoop;
            }

            if (exit.get()) {
                break;
            }
        }
        workQueue();

        exitFuture.complete(null);
    }

    /**
     * @return the current tps
     */
    @Api public int tps() {
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
                gameLauncher.handleError(ex);
            }
        }
    }

    @Override protected CompletableFuture<Void> cleanup0() throws GameException {
        exit.set(true);
        return exitFuture;
    }

    protected void launcherTick() {
        try {
            gameLauncher.tick();
        } catch (Throwable ex) {
            gameLauncher.handleError(ex);
        }
    }

    private void removeOldTicks() {
        long compareTo = System.nanoTime() - second;
        while (!ticks.isEmpty()) {
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
