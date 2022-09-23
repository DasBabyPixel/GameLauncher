package gamelauncher.lwjgl.render.glfw;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengles.GLES32.*;

import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import gamelauncher.engine.render.FrameCounter;
import gamelauncher.engine.render.FrameRenderer;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.render.RenderThread;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.AbstractExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.lwjgl.render.states.GlStates;
import gamelauncher.lwjgl.render.states.StateRegistry;

@SuppressWarnings("javadoc")
@Deprecated
public class GLFWRenderThreadDeprecated extends AbstractExecutorThread implements RenderThread {

	private static final AtomicInteger names = new AtomicInteger();

	final GLFWWindow window;

	final FrameCounter frameCounter;

	final Phaser drawPhaser = new Phaser();

	private final AtomicBoolean viewportChanged = new AtomicBoolean(false);

	private FrameRenderer lastFrameRenderer = null;

	private final AtomicBoolean shouldDraw = new AtomicBoolean(false);

	private final AtomicBoolean refreshAfterDraw = new AtomicBoolean();

	private final AtomicBoolean refresh = new AtomicBoolean(false);

	private final AtomicLong lastResizeRefresh = new AtomicLong(0);

	private final AtomicLong lastActualFrame = new AtomicLong(0);

	private final GLFWGLContext renderingContext;

	private boolean forceTryRender = false;

	private final Consumer<Long> nanoSleeper = nanos -> {
		this.waitForSignalTimeout(nanos);
	};

	public GLFWRenderThreadDeprecated(GLFWWindow window) {
		super(window.getLauncher().getGlThreadGroup());
		this.renderingContext = window.createNewContext();
		this.window = window;
		this.frameCounter = this.window.getFrameCounter();
		this.setName("GLFW-RenderThread-" + names.incrementAndGet());
	}

	@Override
	protected void cleanup0() throws GameException {
		window.removeContext(renderingContext);
		super.cleanup0();
	}

	@Override
	protected void startExecuting() {
		try {
			Threads.waitFor(window.windowCreateFuture());
		} catch (GameException ex) {
			ex.printStackTrace();
		}
		drawPhaser.register();
		viewportChanged();

		StateRegistry.setContextHoldingThread(window.getGLFWId(), Thread.currentThread());
		GlStates.current().enable(GL_DEBUG_OUTPUT);
		GLUtil.setupDebugMessageCallback();

		glfwSwapInterval(0);
	}

	@Override
	protected void stopExecuting() {
		if (lastFrameRenderer != null) {
			try {
				lastFrameRenderer.cleanup(window);
				lastFrameRenderer = null;
			} catch (GameException ex) {
				window.getLauncher().handleError(ex);
			}
		}
		window.getLauncher().getGlThreadGroup().terminated(this);
		try {
			StateRegistry.removeContext(window.getGLFWId());
		} catch (GameException ex) {
			window.getLauncher().handleError(ex);
		}
		StateRegistry.setContextHoldingThread(window.getGLFWId(), null);
	}

	@Override
	protected void workExecution() {
		if (shouldDraw.compareAndSet(true, false) || window.getRenderMode() == RenderMode.CONTINUOUSLY) {
			if (lastResizeRefresh.get() != 0) {
				if (((lastResizeRefresh.get() + TimeUnit.MILLISECONDS.toNanos(50)) - System.nanoTime() > 0)
						&& !(lastActualFrame.get() + TimeUnit.MILLISECONDS.toNanos(0) - System.nanoTime() < 0)) {
					if (window.getRenderMode() != RenderMode.CONTINUOUSLY) {
						shouldDraw.set(true);
						forceTryRender = true;
						return;
					}
				}
			}
			forceTryRender = false;
			refresh.set(false);
			lastActualFrame.set(System.nanoTime());
			frame(Type.RENDER);
			if (refreshAfterDraw.compareAndSet(true, false)) {
				frame(Type.REFRESH);
			}
		} else if (refresh.compareAndSet(true, false)) {
			frame(Type.REFRESH);
		}
	}

	@Override
	protected boolean shouldWaitForSignal() {
		return window.getRenderMode() != RenderMode.CONTINUOUSLY && !forceTryRender;
	}

	private void frame(Type type) {
		FrameRenderer fr = window.getFrameRenderer();
		if (fr != null) {
			if (type == Type.REFRESH) {
				window.manualFramebuffer.query();
				try {
					fr.refreshDisplay(window);
				} catch (GameException ex) {
					window.getLauncher().handleError(ex);
				}
				return;
			}

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

			viewport: if (viewportChanged.compareAndSet(true, false)) {
				window.manualFramebuffer.query();
				if (window.manualFramebuffer.width().intValue() == 0
						|| window.manualFramebuffer.height().intValue() == 0) {
					break viewport;
				}
				try {
					fr.windowSizeChanged(window);
				} catch (Throwable ex) {
					window.getLauncher().handleError(ex);
				}
			}

			frame: {
				if (window.manualFramebuffer.width().intValue() == 0
						|| window.manualFramebuffer.height().intValue() == 0) {
					break frame;
				}
				try {
					fr.renderFrame(window);
				} catch (Throwable ex) {
					window.getLauncher().handleError(ex);
				}
			}
		}

		drawPhaser.arrive();
		frameCounter.frame(nanoSleeper);
	}

	public void resize() {
		try {
			Threads.waitFor(submit(() -> {
				lastResizeRefresh.set(System.nanoTime());
//				frame(Type.RENDER);
				frame(Type.REFRESH);
				shouldDraw.set(true);
			}));
		} catch (GameException ex) {
			ex.printStackTrace();
		}
	}

	public void scheduleDrawRefresh() {
		shouldDraw.set(true);
		refreshAfterDraw.set(true);
		signal();
	}

	public void scheduleDraw() {
		shouldDraw.set(true);
		signal();
	}

	public void refresh() {
		refresh.set(true);
		signal();
	}

	@Override
	public Window getWindow() {
		return window;
	}

	void viewportChanged() {
		this.viewportChanged.set(true);
	}

	private static enum Type {
		RENDER, REFRESH
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
