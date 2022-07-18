package gamelauncher.lwjgl.render.glfw;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengles.GLES20.*;

import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.lwjgl.opengles.GLES;

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

	final GWindow window;
	final Lock shouldDrawLock = new ReentrantLock(true);
	final Condition shouldDrawCondition = shouldDrawLock.newCondition();
	final AtomicBoolean viewportChanged = new AtomicBoolean(true);
	final FrameCounter frameCounter = new FrameCounter();
	final AtomicBoolean hasContext = new AtomicBoolean(false);
	final Lock hasContextLock = new ReentrantLock(true);
	final Condition hasContextCondition = hasContextLock.newCondition();
	final Phaser drawPhaser = new Phaser();
	private FrameRenderer lastFrameRenderer = null;
	private boolean shouldDraw = false;

	public GLFWRenderThread(GWindow window) {
		this.window = window;
		this.setName("GLFW-RenderThread-" + names.incrementAndGet());
	}

	@Override
	protected void startExecuting() {
		Threads.waitFor(window.windowCreateFuture());
		drawPhaser.register();

		glfwMakeContextCurrent(window.getGLFWId());
		GLES.createCapabilities();
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
		glfwMakeContextCurrent(0L);
		GLES.setCapabilities(null);
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
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				lastFrameRenderer = fr;
				if (fr != null) {
					try {
						fr.init(window);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}

			if (viewportChanged.compareAndSet(true, false)) {
				glViewport(0, 0, window.getFramebuffer().width().intValue(),
						window.getFramebuffer().height().intValue());
				try {
					fr.windowSizeChanged(window);
				} catch (GameException ex) {
					ex.printStackTrace();
				}
			}

			try {
				fr.renderFrame(window);
			} catch (Exception ex) {
				ex.printStackTrace();
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
	}

	@Override
	public Window getWindow() {
		return window;
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
