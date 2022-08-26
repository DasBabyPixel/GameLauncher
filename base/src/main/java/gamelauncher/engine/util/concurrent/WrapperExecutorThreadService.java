package gamelauncher.engine.util.concurrent;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameCallable;
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.engine.util.logging.Logger;

/**
 * @author DasBabyPixel
 */
public class WrapperExecutorThreadService implements ExecutorThreadService {

	private static final Logger logger = Logger.getLogger(WrapperExecutorThreadService.class);

	private final CompletableFuture<Void> exitFuture = new CompletableFuture<>();

	private final ExecutorService service;

	private final Waiter waiter;

	/**
	 * @param service
	 */
	public WrapperExecutorThreadService(ExecutorService service) {
		this.service = service;
		this.waiter = new Waiter();
		this.waiter.start();
	}

	@Override
	public CompletableFuture<Void> submit(GameRunnable runnable) {
		return submit(runnable.toCallable());
	}

	@Override
	public <T> CompletableFuture<T> submit(GameCallable<T> callable) {
		WrapperCallable<T> w = new WrapperCallable<>(callable, WrapperEntry.newEntry());
		service.submit(w);
		return w.fut;
	}

	@Override
	public CompletableFuture<Void> exit() {
		service.shutdown();
		return exitFuture;
	}

	@Override
	public CompletableFuture<Void> exitFuture() {
		return exitFuture;
	}

	@Override
	public Collection<GameRunnable> exitNow() {
		Collection<Runnable> col = service.shutdownNow();
		return col.stream()
				.map(r -> (WrapperCallable<?>) r)
				.map(w -> w.callable.toRunnable())
				.collect(Collectors.toList());
	}

	@Override
	public void workQueue() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void park() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unpark() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void park(long nanos) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<Void> submitLast(GameRunnable runnable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Thread thread() {
		throw new UnsupportedOperationException("Not a single thread");
	}

	@Override
	public CompletableFuture<Void> submitFirst(GameRunnable runnable) {
		throw new UnsupportedOperationException();
	}

	static class WrapperCallable<T> implements Runnable {

		static final ThreadLocal<WrapperCallable<?>> threadLocal = new ThreadLocal<>();

		final GameCallable<T> callable;

		final WrapperEntry entry;

		final CompletableFuture<T> fut = new CompletableFuture<>();

		public WrapperCallable(GameCallable<T> callable, WrapperEntry entry) {
			this.callable = callable;
			this.entry = entry;
		}

		@Override
		public void run() {
			try {
				if (threadLocal.get() != null) {
					throw new IllegalStateException("Shouldn't happen");
				}
				threadLocal.set(this);
				fut.complete(callable.call());
			} catch (Throwable ex) {
				GameException ex2 = buildStacktrace();
				ex2.initCause(ex);
				logger.error(ex2);
				fut.completeExceptionally(ex2);
			} finally {
				threadLocal.remove();
			}
		}

		public GameException buildStacktrace() {
			GameException ex = new GameException("Exception in ExecutorService");
			if (entry != null) {
				Throwable t = entry.calculateCause();
				if (t != null)
					ex.addSuppressed(t);
			}
			return ex;
		}

	}

	static final AtomicInteger id = new AtomicInteger();

	class Waiter extends Thread {

		public Waiter() {
			super("Waiter-" + id.incrementAndGet());
			setDaemon(true);
		}

		@Override
		public void run() {
			while (!service.isShutdown()) {
				try {
					service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			exitFuture.complete(null);
		}

	}

}
