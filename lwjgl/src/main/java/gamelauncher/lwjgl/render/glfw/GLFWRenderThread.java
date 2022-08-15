package gamelauncher.lwjgl.render.glfw;

import static org.lwjgl.glfw.GLFW.*;

import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import gamelauncher.engine.render.FrameCounter;
import gamelauncher.engine.render.FrameRenderer;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.render.RenderThread;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.AbstractExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.lwjgl.render.states.StateRegistry;

@SuppressWarnings("javadoc")
public class GLFWRenderThread extends AbstractExecutorThread implements RenderThread {

	private static final AtomicInteger names = new AtomicInteger();

	final GLFWWindow window;
	final Lock shouldDrawLock = new ReentrantLock(true);
	final Condition shouldDrawCondition = shouldDrawLock.newCondition();
	final FrameCounter frameCounter;
	final AtomicBoolean hasContext = new AtomicBoolean(false);
	final Lock hasContextLock = new ReentrantLock(true);
	final Condition hasContextCondition = hasContextLock.newCondition();
	final Phaser drawPhaser = new Phaser();
	private final AtomicBoolean viewportChanged = new AtomicBoolean(false);
	private FrameRenderer lastFrameRenderer = null;
	private boolean shouldDraw = false;

	public GLFWRenderThread(GLFWWindow window) {
		super(window.getLauncher().getGlThreadGroup());
		this.window = window;
		this.frameCounter = this.window.getFrameCounter();
		this.setName("GLFW-RenderThread-" + names.incrementAndGet());
	}

	@Override
	protected void startExecuting() {
		Threads.waitFor(window.windowCreateFuture());
		drawPhaser.register();
		viewportChanged();

		StateRegistry.setContextHoldingThread(window.getGLFWId(), Thread.currentThread());
		hasContext.set(true);
		glfwSwapInterval(0);
	}

	@Override
	protected void stopExecuting() {
		if (lastFrameRenderer != null) {
			try {
				lastFrameRenderer.cleanup(window);
				lastFrameRenderer = null;
			} catch (GameException ex) {
				ex.printStackTrace();
			}
		}
		window.getLauncher().getGlThreadGroup().terminated(this);
		StateRegistry.setContextHoldingThread(window.getGLFWId(), null);
	}

	@Override
	protected void workExecution() {
		shouldDrawLock.lock();
		if (shouldDraw()) {
			shouldDraw = false;
			shouldDrawLock.unlock();
			frame();
		} else {
			shouldDrawCondition.awaitUninterruptibly();
			shouldDrawLock.unlock();
		}
	}

	@Override
	protected void signal() {
		super.signal();
		shouldDrawLock.lock();
		shouldDrawCondition.signal();
		shouldDrawLock.unlock();
		frameCounter.stopWaiting();
	}

	@Override
	protected void loop() {
		workQueue();
		hasContextLock.lock();
		if (!hasContext.get()) {
			hasContextCondition.awaitUninterruptibly();
		}
		workExecution();
		hasContextLock.unlock();
	}
	
	@Override
	protected boolean shouldHandle(QueueEntry entry) {
		if (entry.run instanceof ContextlessGameRunnable) {
			return true;
		}
		if (!hasContext.get()) {
			return false;
		}
		return super.shouldHandle(entry);
	}

	private void frame() {
		FrameRenderer fr = window.getFrameRenderer();
		if (fr != null) {
			if (lastFrameRenderer != fr) {
				if (lastFrameRenderer != null) {
					try {
						lastFrameRenderer.cleanup(window);
					} catch (Throwable ex) {
						window.getLauncher().handleError(ex);
					}
				}
				lastFrameRenderer = fr;
				if (fr != null) {
					try {
						fr.init(window);
					} catch (Throwable ex) {
						window.getLauncher().handleError(ex);
					}
				}
			}

			if (viewportChanged.compareAndSet(true, false)) {
				window.manualFramebuffer.query();
				try {
					fr.windowSizeChanged(window);
				} catch (Throwable ex) {
					window.getLauncher().handleError(ex);
				}
			}

			try {
				fr.renderFrame(window);
			} catch (Throwable ex) {
				window.getLauncher().handleError(ex);
			}
		}

		drawPhaser.arrive();
		if (hasContext.get()) {
			frameCounter.frame();
		} else {
			frameCounter.frameNoWait();
		}
	}

	private boolean shouldDraw() {
		shouldDrawLock.lock();
		try {
			RenderMode mode = window.getRenderMode();
			if (mode == RenderMode.CONTINUOUSLY) {
				return true;
			}
			return shouldDraw;
		} finally {
			shouldDrawLock.unlock();
		}
	}

	public void scheduleDraw() {
		shouldDrawLock.lock();
		shouldDraw = true;
		shouldDrawCondition.signalAll();
		shouldDrawLock.unlock();
		signal();
	}

	@Override
	public Window getWindow() {
		return window;
	}

	void viewportChanged() {
		this.viewportChanged.set(true);
	}

//	@Override
//	public void run() {
//
//		while (!close.get()) {
//			workQueue();
//			if (!hasContext.get()) {
//				hasContextLock.lock();
//				hasContextCondition.awaitUninterruptibly();
//				hasContextLock.unlock();
//				continue;
//			}
//			if (shouldDraw()) {
//				frame();
//			} else {
//				shouldDrawLock.readLock().lock();
//				if (shouldDraw()) {
//					shouldDrawLock.readLock().unlock();
//					continue;
//				}
//				shouldDrawCondition.awaitUninterruptibly();
//				shouldDrawLock.readLock().unlock();
//			}
//		}
//		if (lastFrameRenderer != null) {
//			try {
//				lastFrameRenderer.cleanup(window);
//			} catch (GameException ex) {
//				ex.printStackTrace();
//			}
//			lastFrameRenderer = null;
//		}
//	}

//	public void bindContext() {
//		if (Thread.currentThread() == this)
//			return;
//		CompletableFuture<Void> f = submitFirst(new ContextlessGameRunnable() {
//			@Override
//			public void run() throws GameException {
//				hasContext.set(false);
//				glfwMakeContextCurrent(0);
//				GLES.setCapabilities(null);
//				StateRegistry.setContextHoldingThread(window.getGLFWId(), null);
//			}
//		});
//		Threads.waitFor(f);
//		glfwMakeContextCurrent(window.getGLFWId());
//		GLES.createCapabilities();
//		StateRegistry.setContextHoldingThread(window.getGLFWId(), Thread.currentThread());
//	}
//
//	public void releaseContext() {
//		if (Thread.currentThread() == this)
//			return;
//		glfwMakeContextCurrent(0);
//		GLES.setCapabilities(null);
//		StateRegistry.setContextHoldingThread(window.getGLFWId(), null);
//		CompletableFuture<Void> f = submitFirst(new ContextlessGameRunnable() {
//			@Override
//			public void run() throws GameException {
//				glfwMakeContextCurrent(window.getGLFWId());
//				GLES.createCapabilities();
//				StateRegistry.setContextHoldingThread(window.getGLFWId(), Thread.currentThread());
//				hasContext.set(true);
//			}
//		});
//		hasContextLock.lock();
//		hasContextCondition.signalAll();
//		hasContextLock.unlock();
//		Threads.waitFor(f);
//	}

//	@Override
//	public void workQueue() {
//		if (window.renderThreadFutures.isEmpty()) {
//			return;
//		}
//		GLFWWindow.Future f;
//		while ((f = window.renderThreadFutures.poll()) != null) {
//			try {
//				if (!hasContext.get()) {
//					if (!(f.r instanceof ContextlessGameRunnable)) {
//						window.renderThreadFutures.offerFirst(f);
//						return;
//					}
//				}
//				f.r.run();
//				f.f.complete(null);
//			} catch (Throwable ex) {
//				f.f.completeExceptionally(ex);
//				window.launcher.handleError(ex);
//			}
//		}
//	}
}
