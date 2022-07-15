package gamelauncher.lwjgl.render.glfw;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengles.GLES20.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
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
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.lwjgl.render.states.StateRegistry;

@SuppressWarnings("javadoc")
public class GLFWRenderThread extends Thread implements RenderThread {

	final GLFWWindow window;
	final AtomicBoolean shouldDraw = new AtomicBoolean(false);
	final Lock shouldDrawLock = new ReentrantLock(true);
	final Condition shouldDrawCondition = shouldDrawLock.newCondition();
	final AtomicBoolean close = new AtomicBoolean(false);
	final AtomicBoolean viewportChanged = new AtomicBoolean(true);
	final CompletableFuture<Void> closeFuture = new CompletableFuture<>();
	final FrameCounter frameCounter = new FrameCounter();
	final AtomicBoolean hasContext = new AtomicBoolean(false);
	final Lock hasContextLock = new ReentrantLock(true);
	final Condition hasContextCondition = hasContextLock.newCondition();

	public GLFWRenderThread(GLFWWindow window) {
		this.window = window;
	}

	@Override
	public void run() {

		try {
			window.windowCreateFuture.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		window.drawPhaser.register();

		glfwMakeContextCurrent(window.getId());
//		GL.createCapabilities();
		GLES.createCapabilities();
		StateRegistry.setContextHoldingThread(window.getId(), Thread.currentThread());
		hasContext.set(true);
		glfwSwapInterval(0);
		scheduleDraw();
		while (!close.get()) {
			workQueue();
			if (!hasContext.get()) {
				hasContextLock.lock();
				hasContextCondition.awaitUninterruptibly();
				hasContextLock.unlock();
				continue;
			}
			if (shouldDraw()) {
				frame();
			} else {
				shouldDrawLock.lock();
				if (shouldDraw()) {
					shouldDrawLock.unlock();
					continue;
				}
				shouldDrawCondition.awaitUninterruptibly();
				shouldDrawLock.unlock();
			}
		}
		if (lastFrameRenderer != null) {
			try {
				lastFrameRenderer.cleanup(window);
			} catch (GameException ex) {
				ex.printStackTrace();
			}
			lastFrameRenderer = null;
		}
		glfwMakeContextCurrent(0L);
//		GL.setCapabilities(null);
		GLES.setCapabilities(null);
		closeFuture.complete(null);
	}

	public void bindContext() {
		if (Thread.currentThread() == this)
			return;
		CompletableFuture<Void> f = submitFirst(new ContextlessGameRunnable() {
			@Override
			public void run() throws GameException {
				hasContext.set(false);
				glfwMakeContextCurrent(0);
				GLES.setCapabilities(null);
				StateRegistry.setContextHoldingThread(window.getId(), null);
			}
		});
		Threads.waitFor(f);
		glfwMakeContextCurrent(window.getId());
		GLES.createCapabilities();
		StateRegistry.setContextHoldingThread(window.getId(), Thread.currentThread());
	}

	public void releaseContext() {
		if (Thread.currentThread() == this)
			return;
		glfwMakeContextCurrent(0);
		GLES.setCapabilities(null);
		StateRegistry.setContextHoldingThread(window.getId(), null);
		CompletableFuture<Void> f = submitFirst(new ContextlessGameRunnable() {
			@Override
			public void run() throws GameException {
				glfwMakeContextCurrent(window.getId());
				GLES.createCapabilities();
				StateRegistry.setContextHoldingThread(window.getId(), Thread.currentThread());
				hasContext.set(true);
			}
		});
		hasContextLock.lock();
		hasContextCondition.signalAll();
		hasContextLock.unlock();
		Threads.waitFor(f);
	}

	public void scheduleDraw() {
		shouldDrawLock.lock();
		shouldDraw.set(true);
		shouldDrawCondition.signalAll();
		shouldDrawLock.unlock();
	}

	private FrameRenderer lastFrameRenderer = null;

	void frame() {
		shouldDrawLock.lock();
		shouldDraw.set(false);
		shouldDrawLock.unlock();

		FrameRenderer fr = window.frameRenderer.get();
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

		window.drawPhaser.arrive();
		if (hasContext.get()) {
			frameCounter.frame();
		} else {
			frameCounter.frameNoWait();
		}
	}

	private boolean shouldDraw() {
		shouldDrawLock.lock();
		try {
			RenderMode mode = window.renderMode.get();
			if (mode == RenderMode.CONTINUOUSLY) {
				return true;
			}
			return shouldDraw.get();
		} finally {
			shouldDrawLock.unlock();
		}
	}

	@Override
	public void workQueue() {
		if (window.renderThreadFutures.isEmpty()) {
			return;
		}
		GLFWWindow.Future f;
		while ((f = window.renderThreadFutures.poll()) != null) {
			try {
				if (!hasContext.get()) {
					if (!(f.r instanceof ContextlessGameRunnable)) {
						window.renderThreadFutures.offerFirst(f);
						return;
					}
				}
				f.r.run();
				f.f.complete(null);
			} catch (Throwable ex) {
				f.f.completeExceptionally(ex);
				window.launcher.handleError(ex);
			}
		}
	}

	public CompletableFuture<Void> submitFirst(GameRunnable r) {
		CompletableFuture<Void> f = new CompletableFuture<>();
		window.renderThreadFutures.offerFirst(new GLFWWindow.Future(f, r));
		shouldDrawLock.lock();
		shouldDrawCondition.signalAll();
		shouldDrawLock.unlock();
		return f;
	}

	@Override
	public CompletableFuture<Void> submit(GameRunnable r) {
		CompletableFuture<Void> f = new CompletableFuture<>();
		window.renderThreadFutures.offer(new GLFWWindow.Future(f, r));
		shouldDrawLock.lock();
		shouldDrawCondition.signalAll();
		shouldDrawLock.unlock();
		return f;
	}

	@Override
	public Window getWindow() {
		return window;
	}

}
