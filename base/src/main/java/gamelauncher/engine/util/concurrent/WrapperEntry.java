package gamelauncher.engine.util.concurrent;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
@SuppressWarnings("javadoc")
public class WrapperEntry {

	public StackTraceElement[] stacktrace;

	public WrapperEntry cause;

	public Thread thread;

	public WrapperEntry(StackTraceElement[] stacktrace, WrapperEntry cause, Thread thread) {
		this.stacktrace = stacktrace;
		this.cause = cause;
		this.thread = thread;
	}

	public Throwable calculateCause() {
		GameException c = new GameException("Thread: " + thread.getName());
		c.setStackTrace(stacktrace);
		if (cause != null) {
			Throwable cause = this.cause.calculateCause();
			if (cause != null)
				c.initCause(cause);
		}
		return c;
	}

	@Override
	public String toString() {
		return "WrapperEntry [cause=" + cause + ", thread=" + thread.getName() + "]";
	}

	public static WrapperEntry newEntry() {
		if (Threads.calculateThreadStacks) {
			return new WrapperEntry(new Throwable().getStackTrace(), cause(), Thread.currentThread());
		}
		return new WrapperEntry(null, cause(), Thread.currentThread());
	}

	private static WrapperEntry cause() {
		if (Thread.currentThread() instanceof AbstractExecutorThread) {
			AbstractExecutorThread aet = (AbstractExecutorThread) Thread.currentThread();
			return aet.currentEntry;
		}
		WrapperExecutorThreadService.WrapperCallable<?> callable = WrapperExecutorThreadService.WrapperCallable.threadLocal
				.get();
		if (callable != null) {
			return callable.entry;
		}
		return null;
	}

}
