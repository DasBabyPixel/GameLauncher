package gamelauncher.engine;

import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.collection.AtomicConcurrentLinkedDeque;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameCallable;
import gamelauncher.engine.util.function.GameRunnable;

/**
 * @author DasBabyPixel
 */
public class GameThread extends Thread {

	private final AtomicBoolean exit = new AtomicBoolean(false);

	private final CompletableFuture<Void> exitFuture = new CompletableFuture<>();

	private final Queue<GameRunnable> queue = new ConcurrentLinkedQueue<>();

	private final Deque<Long> ticks = new AtomicConcurrentLinkedDeque<>();

	private final Deque<Long> tickTimes = new AtomicConcurrentLinkedDeque<>();

	private final GameLauncher gameLauncher;

	private final AtomicInteger tick = new AtomicInteger(0);

	private final long second = TimeUnit.SECONDS.toNanos(1);

	private final long tickTime = (long) (second / GameLauncher.MAX_TPS);

	private final AtomicLong lastTick = new AtomicLong(-1L);

	private volatile long ticksTimeSum = 0;

	private volatile long ticksTimeSumCount = 0;

	/**
	 * @param launcher
	 */
	public GameThread(GameLauncher launcher) {
		this.gameLauncher = launcher;
		this.setName("GameThread");
		this.setPriority(MAX_PRIORITY);
	}

	/**
	 * Computes the partial tick
	 * 
	 * @return the current partial tick
	 */
	public float getPartialTick() {
		long lastTick = this.lastTick.get();
		if (lastTick == -1) {
			return 0;
		}
		long nanos = System.nanoTime();
		long diff = nanos - lastTick;
		return diff / (float) tickTime;
	}

	@Override
	public void run() {
		// t1 - t2 < 0 | t1 < t2
		workQueue();
		long lastTick = System.nanoTime();
		long tpsTrySkipCounter = 0;

		mainLoop: while (true) {
			long nanoDelay = lastTick - System.nanoTime();
			if (nanoDelay > 0) {
				Threads.park(nanoDelay);
			}
			while (lastTick - System.nanoTime() < 0) {
				int tps = getTps();
				if (tps < GameLauncher.MAX_TPS * 0.9) {
					tpsTrySkipCounter++;
					if (tpsTrySkipCounter > GameLauncher.MAX_TPS && tpsTrySkipCounter > 1) {
						gameLauncher.getLogger().infof("Low TPS!");
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
				tickTimes.offer(tickTook);
				ticksTimeSum += tickTook;
				ticksTimeSumCount++;
				if (tickTook > tickTime) {
					long tooLong = tickTook - tickTime;
					long ticksToSkip = tooLong / tickTime;
					gameLauncher.getLogger()
							.infof("Tick took %sms. This is %sms longer than expected!%s",
									TimeUnit.NANOSECONDS.toMillis(tickTook), TimeUnit.NANOSECONDS.toMillis(tooLong),
									ticksToSkip > 0 ? String.format("%nSkipping %s ticks to compensate", ticksToSkip)
											: "");
					if (ticksToSkip > 0) {
						lastTick += (ticksToSkip - 1) * tickTime;
					}
				}
				if (exit.get()) {
					break mainLoop;
				}
			}

			if (exit.get()) {
				break mainLoop;
			}
		}
		workQueue();

		exitFuture.complete(null);
	}

	private void removeOldTicks() {
		long compareTo = System.nanoTime() - second;
		while (!ticks.isEmpty()) {
			long first = ticks.peekFirst();
			if (compareTo - first > 0) {
				ticks.pollFirst();
				long took = tickTimes.pollFirst();
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
		ticks.offer(nanos);
	}

	private void tick(long tickTime) {
		offerTick(tickTime);
		tick.incrementAndGet();
		try {
			gameLauncher.tick();
		} catch (Throwable ex) {
			gameLauncher.handleError(ex);
		}
		workQueue();
	}

	/**
	 * @return the current tps
	 */
	public int getTps() {
		return ticks.size();
	}

	/**
	 * @return the average tick time over the last second
	 */
	public double getAverageTickTime() {
		return (double) ticksTimeSum / (double) ticksTimeSumCount;
	}

	/**
	 * Runs a {@link GameRunnable} on the {@link GameThread}
	 * 
	 * @param runnable
	 * @return a completionFuture
	 */
	public CompletableFuture<Void> runLater(GameRunnable runnable) {
		return runLater(() -> {
			runnable.run();
			return null;
		});
	}

	/**
	 * Runs a {@link GameCallable} on the {@link GameThread}
	 * 
	 * @param <T>
	 * @param callable
	 * @return a completionFuture
	 */
	public <T> CompletableFuture<T> runLater(GameCallable<T> callable) {
		CompletableFuture<T> future = new CompletableFuture<>();
		runLater(callable, future);
		return future;
	}

	private <T> void runLater(GameCallable<T> callable, CompletableFuture<T> future) {
		queue.offer(new GameRunnable() {

			@Override
			public void run() throws GameException {
				try {
					T t = callable.call();
					future.complete(t);
				} catch (Throwable th) {
					future.completeExceptionally(th);
					throw new GameException(th);
				}
			}

		});
	}

	private void workQueue() {
		GameRunnable runnable;
		while ((runnable = queue.poll()) != null) {
			try {
				runnable.run();
			} catch (GameException ex) {
				gameLauncher.handleError(ex);
			}
		}
	}

	/**
	 * @return the current tick
	 */
	public int getCurrentTick() {
		return tick.get();
	}

	/**
	 * Exits the {@link GameLauncher}
	 * 
	 * @return a completionFuture
	 */
	public CompletableFuture<Void> exit() {
		exit.set(true);
		return exitFuture;
	}

	/**
	 * @return the ExitFuture
	 */
	public CompletableFuture<Void> getExitFuture() {
		return exitFuture;
	}

}
