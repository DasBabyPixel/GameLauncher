package gamelauncher.lwjgl.render.glfw;

import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import gamelauncher.engine.render.FrameCounter;
import gamelauncher.engine.render.FrameRenderer;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.render.RenderThread;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.lwjgl.render.states.StateRegistry;

@SuppressWarnings("javadoc")
public class GLFWRenderThread extends LWJGLAsyncOpenGL implements RenderThread {

	private volatile boolean shouldDraw = false;

	private volatile boolean refreshAfterDraw = false;

	private volatile boolean refresh = false;

	private volatile boolean viewportChanged = false;

	private volatile long lastResizeRefresh = 0;

	private volatile long lastActualFrame = 0;

	final FrameCounter frameCounter;

	final Phaser drawPhaser = new Phaser();

	private final GLFWWindow window;

	private FrameRenderer lastFrameRenderer = null;

	private boolean forceTryRender = false;

	private final Consumer<Long> nanoSleeper = nanos -> {
		this.waitForSignalTimeout(nanos);
	};

	public GLFWRenderThread(GLFWWindow window) {
		super(window.getLauncher(), window);
		this.window = window;
		this.frameCounter = window.frameCounter;
		setName("GLFW-RenderThread");
	}

	@Override
	protected void startExecuting() {
		super.startExecuting();
		try {
			Threads.waitFor(window.windowCreateFuture());
		} catch (GameException ex) {
			ex.printStackTrace();
		}
		drawPhaser.register();
		viewportChanged();
	}

	@Override
	protected void stopExecuting() {
		if (lastFrameRenderer != null) {
			try {
				lastFrameRenderer.cleanup(window);
				lastFrameRenderer = null;
			} catch (GameException ex) {
				launcher.handleError(ex);
			}
		}
		launcher.getGlThreadGroup().terminated(this);
		try {
			StateRegistry.removeContext(window.glfwId);
		} catch (GameException ex) {
			launcher.handleError(ex);
		}
		StateRegistry.setContextHoldingThread(window.glfwId, null);
	}

	@Override
	protected void workExecution() {
		if (shouldDraw || window.getRenderMode() == RenderMode.CONTINUOUSLY) {
			shouldDraw = false;
			if (lastResizeRefresh != 0) {
				if (((lastResizeRefresh + TimeUnit.MILLISECONDS.toNanos(50)) - System.nanoTime() > 0)
						&& !(lastActualFrame + TimeUnit.MILLISECONDS.toNanos(0) - System.nanoTime() < 0)) {
					if (window.getRenderMode() != RenderMode.CONTINUOUSLY) {
						shouldDraw = true;
						forceTryRender = true;
						return;
					}
				}
			}
			forceTryRender = false;
			refresh = false;
			lastActualFrame = System.nanoTime();
			frame(Type.RENDER);
			if (refreshAfterDraw) {
				refreshAfterDraw = false;
				frame(Type.REFRESH);
			}
		} else if (refresh) {
			refresh = false;
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

			viewport: if (viewportChanged) {
				viewportChanged = false;
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
				lastResizeRefresh = System.nanoTime();
//				frame(Type.RENDER);
				frame(Type.REFRESH);
				shouldDraw = true;
			}));
		} catch (GameException ex) {
			ex.printStackTrace();
		}
	}

	public void scheduleDrawRefresh() {
		shouldDraw = true;
		refreshAfterDraw = true;
		signal();
	}

	public void scheduleDraw() {
		shouldDraw = true;
		signal();
	}

	public void refresh() {
		refresh = true;
		signal();
	}

	public void viewportChanged() {
		viewportChanged = true;
	}

	private static enum Type {
		RENDER, REFRESH
	}

	@Override
	public Window getWindow() {
		return window;
	}

}
