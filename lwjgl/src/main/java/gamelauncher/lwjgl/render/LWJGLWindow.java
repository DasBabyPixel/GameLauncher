package gamelauncher.lwjgl.render;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.lwjgl.glfw.GLFWCharCallbackI;
import org.lwjgl.glfw.GLFWCursorEnterCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.lwjgl.glfw.GLFWWindowCloseCallbackI;
import org.lwjgl.glfw.GLFWWindowPosCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.opengl.GL;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.FrameCounter;
import gamelauncher.engine.render.FrameRenderer;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.render.RenderThread;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameCallable;
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.input.LWJGLInput;
import gamelauncher.lwjgl.input.LWJGLMouse;
import gamelauncher.lwjgl.render.framebuffer.WindowFramebuffer;

/**
 * @author DasBabyPixel
 *
 */
public class LWJGLWindow implements Window {

	private final Logger logger;

	private static final AtomicLong names = new AtomicLong(0);

	private final AtomicLong id = new AtomicLong();
	private final AtomicLong name = new AtomicLong();
	private final NumberValue width = NumberValue.zero();
	private final NumberValue height = NumberValue.zero();
//	private final NumberValue framebufferWidth = NumberValue.zero();
//	private final NumberValue framebufferHeight = NumberValue.zero();
	private final AtomicInteger x = new AtomicInteger();
	private final AtomicInteger y = new AtomicInteger();
	private final AtomicReference<String> title = new AtomicReference<>();
	private final LWJGLMouse mouse = new LWJGLMouse(this);
	private final AtomicReference<FrameRenderer> frameRenderer = new AtomicReference<>(null);
	private final AtomicBoolean floating = new AtomicBoolean(false);
	private final AtomicBoolean decorated = new AtomicBoolean(true);
	private final AtomicReference<RenderMode> renderMode = new AtomicReference<>(RenderMode.CONTINUOUSLY);
	private final WindowThread windowThread = new WindowThread();
	private final AtomicReference<CompletableFuture<WindowThread>> windowThreadCreateFuture = new AtomicReference<>(
			new CompletableFuture<>());
	private final AtomicBoolean startRenderer = new AtomicBoolean(false);
	private final LRenderThread renderThread = new LRenderThread();
	private final Queue<Future> windowThreadFutures = new ConcurrentLinkedQueue<>();
	private final Queue<Future> renderThreadFutures = new ConcurrentLinkedQueue<>();
//	private final LWJGLDrawContext context = new LWJGLDrawContext(this);
	private final Framebuffer framebuffer = new WindowFramebuffer();
	private final LWJGLInput input;
	private final CompletableFuture<Window> closeFuture = new CompletableFuture<>();
	private final Phaser drawPhaser = new Phaser();
	private final AtomicBoolean swapBuffers = new AtomicBoolean(false);
	private final GameLauncher launcher;
	private final AtomicReference<CloseCallback> closeCallback = new AtomicReference<>(new CloseCallback() {
		@Override
		public void close() {
			windowThread.close.set(true);
		}
	});

	/**
	 * @param launcher
	 * @param width
	 * @param height
	 * @param title
	 */
	public LWJGLWindow(GameLauncher launcher, int width, int height, String title) {
		this.launcher = launcher;
		this.input = new LWJGLInput(this);
		this.width.setNumber(width);
		this.height.setNumber(height);
		this.title.set(title);
		this.name.set(names.incrementAndGet());
		this.logger = Logger.getLogger(getClass().getSimpleName() + "-" + name.get());
		this.windowThread.setName("WindowThread-" + name.get());
		this.renderThread.setName("RenderThread-" + name.get());
	}

	@Override
	public Framebuffer getFramebuffer() {
		return framebuffer;
	}

	/**
	 * @return the GLFW id of the window
	 */
	public long getId() {
		return id.get();
	}

	/**
	 * @param swap
	 */
	public void swapBuffers(boolean swap) {
		this.swapBuffers.set(swap);
	}

	/**
	 * @return the width property
	 */
	public NumberValue width() {
		return width;
	}

	/**
	 * @return the height property
	 */
	public NumberValue height() {
		return height;
	}

	@Override
	public GameLauncher getLauncher() {
		return launcher;
	}

//	@Override
//	public NumberValue framebufferHeight() {
//		return framebufferHeight;
//	}
//
//	@Override
//	public NumberValue framebufferWidth() {
//		return framebufferWidth;
//	}

	@Override
	public void setFrameRenderer(FrameRenderer renderer) {
		this.frameRenderer.set(renderer);
	}

//	@Override
//	public LWJGLDrawContext getContext() {
//		return context;
//	}

	@Override
	public LWJGLInput getInput() {
		return input;
	}

	@Override
	public CompletableFuture<Window> windowCloseFuture() {
		return closeFuture;
	}

	/**
	 * Sets the title of this window
	 * 
	 * @param title
	 * @return a completionFuture
	 */
	public CompletableFuture<Void> setTitle(String title) {
		return later(() -> {
			glfwSetWindowTitle(id.get(), title);
			LWJGLWindow.this.title.set(title);
		});
	}

	/**
	 * @return the {@link CloseCallback} of this window
	 */
	public CloseCallback getCloseCallback() {
		return closeCallback.get();
	}

	/**
	 * Sets the {@link CloseCallback} of this window
	 * 
	 * @param closeCallback
	 */
	public void setCloseCallback(CloseCallback closeCallback) {
		this.closeCallback.set(closeCallback);
	}

	/**
	 * Runs this {@link Runnable} on the Render Thread
	 * 
	 * @param runnable
	 * @return a completionFuture
	 */
	public CompletableFuture<Void> renderLater(Runnable runnable) {
		LRenderThread thread = renderThread;
		return thread.later(runnable);
	}

	/**
	 * Runs this {@link Runnable} on the Window Thread
	 * 
	 * @param runnable
	 * @return a completionFuture
	 */
	public CompletableFuture<Void> later(Runnable runnable) {
		WindowThread thread = windowThread;
		return thread.later(runnable);
	}

//	@Override
//	public int getFramebufferHeight() {
//		return framebufferHeight.intValue();
//	}
//
//	@Override
//	public int getFramebufferWidth() {
//		return framebufferWidth.intValue();
//	}

	/**
	 * @return if this window is closed
	 */
	public boolean isClosed() {
		WindowThread t = windowThread;
		return t.closeFuture.isDone();
	}

	/**
	 * @return if this window is floating
	 */
	public boolean isFloating() {
		return floating.get();
	}

	/**
	 * @return the Mouse
	 */
	public LWJGLMouse getMouse() {
		return mouse;
	}

	@Override
	public void beginFrame() {
	}

	@Override
	public void endFrame() {
		if (swapBuffers.get()) {
			glfwSwapBuffers(id.get());
		}
	}

	/**
	 * Shows this window and immediatly swaps the buffers
	 * 
	 * @return a completionFuture
	 */
	public CompletableFuture<Void> showAndEndFrame() {
		return later(() -> {
			LRenderThread rt = renderThread;
			rt.bindContext();
			glfwShowWindow(id.get());
			glfwSwapBuffers(id.get());
			rt.releaseContext();
		});
	}

	/**
	 * @return if this window is decorated
	 */
	public boolean isDecorated() {
		return decorated.get();
	}

	/**
	 * Sets if this window is decorated
	 * 
	 * @param decorated
	 */
	public void setDecorated(boolean decorated) {
		if (this.decorated.compareAndSet(!decorated, decorated)) {
			later(() -> glfwSetWindowAttrib(id.get(), GLFW_DECORATED, decorated ? GLFW_TRUE : GLFW_FALSE));
		}
	}

	/**
	 * Sets if this window is floating
	 * 
	 * @param floating
	 */
	public void setFloating(boolean floating) {
		if (this.floating.compareAndSet(!floating, floating)) {
			later(() -> glfwSetWindowAttrib(id.get(), GLFW_FLOATING, floating ? GLFW_TRUE : GLFW_FALSE));
		}
	}

	/**
	 * Sets the position of this window
	 * 
	 * @param x
	 * @param y
	 */
	public void setPosition(int x, int y) {
		later(() -> glfwSetWindowPos(id.get(), x, y));
	}

	/**
	 * Sets the size of this window
	 * 
	 * @param w
	 * @param h
	 */
	public void setSize(int w, int h) {
		later(() -> glfwSetWindowSize(id.get(), w, h));
	}

	/**
	 * Shows this window
	 * 
	 * @return a completionFuture
	 */
	public CompletableFuture<Void> show() {
		return later(() -> glfwShowWindow(id.get()));
	}

	/**
	 * Focuses this window
	 * 
	 * @return a completionFuture
	 */
	public CompletableFuture<Void> forceFocus() {
		return later(() -> {
			glfwRequestWindowAttention(id.get());
			glfwFocusWindow(id.get());
		});
	}

	/**
	 * Hides this window
	 * 
	 * @return a completionFuture
	 */
	public CompletableFuture<Void> hide() {
		return later(() -> glfwHideWindow(id.get()));
	}

	/**
	 * @return the {@link RenderMode} of this window
	 */
	@Override
	public RenderMode getRenderMode() {
		return renderMode.get();
	}

	/**
	 * Sets the {@link RenderMode} of this window
	 * 
	 * @param mode
	 */
	@Override
	public void setRenderMode(RenderMode mode) {
		renderMode.set(mode);
		LRenderThread t = renderThread;
		t.scheduleDraw();
	}

	/**
	 * Creates the window
	 */
	public void createWindow() {
		windowThread.start();
		try {
			windowThreadCreateFuture.get().get();
			if (startRenderer.get()) {
				startRendering0();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the window
	 */
	public void closeWindow() {
		stopRendering();
		windowThread.close.set(true);
		try {
			windowThread.closeFuture.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts rendering
	 */
	public void startRendering() {
		startRenderer.set(true);
		if (windowThreadCreateFuture.get().isDone()) {
			startRendering0();
		}
	}

	private void startRendering0() {
		renderThread.start();
	}

	/**
	 * Stops rendering
	 */
	public void stopRendering() {
		startRenderer.set(false);
		renderThread.close.set(true);
		renderThread.shouldDrawLock.lock();
		renderThread.shouldDrawCondition.signalAll();
		renderThread.shouldDrawLock.unlock();
		try {
			renderThread.closeFuture.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void waitForFrame() {
		drawPhaser.awaitAdvance(drawPhaser.getPhase());
	}

	@Override
	public void scheduleDrawAndWaitForFrame() {
		int phase = drawPhaser.getPhase();
		scheduleDraw();
		drawPhaser.awaitAdvance(phase);
	}

	/**
	 * @return the {@link FrameCounter} object, if one present
	 */
	public FrameCounter getFrameCounter() {
		return renderThread.frameCounter;
	}

	/**
	 * Sets the size limits of this window
	 * 
	 * @param minw minimal width
	 * @param minh minimal height
	 * @param maxw maximal width
	 * @param maxh maximal height
	 * @return a completionFuture
	 */
	public CompletableFuture<Void> setLimits(int minw, int minh, int maxw, int maxh) {
		return later(() -> glfwSetWindowSizeLimits(id.get(), minw, minh, maxw, maxh));
	}

	@Override
	public void scheduleDraw() {
		renderThread.scheduleDraw();
	}

	private class LRenderThread extends Thread implements RenderThread {

		private final AtomicBoolean shouldDraw = new AtomicBoolean(false);
		private final Lock shouldDrawLock = new ReentrantLock(true);
		private final Condition shouldDrawCondition = shouldDrawLock.newCondition();
		private final AtomicBoolean close = new AtomicBoolean(false);
		private final AtomicBoolean viewportChanged = new AtomicBoolean(true);
		private final CompletableFuture<Void> closeFuture = new CompletableFuture<>();
		private final FrameCounter frameCounter = new FrameCounter();
		private final AtomicBoolean hasContext = new AtomicBoolean(false);
		private final Lock hasContextLock = new ReentrantLock(true);
		private final Condition hasContextCondition = hasContextLock.newCondition();

		@Override
		public void run() {

			try {
				windowThreadCreateFuture.get().get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			drawPhaser.register();
			glfwMakeContextCurrent(id.get());
			GL.createCapabilities();
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
					lastFrameRenderer.cleanup(LWJGLWindow.this);
				} catch (GameException ex) {
					ex.printStackTrace();
				}
				lastFrameRenderer = null;
			}
			glfwMakeContextCurrent(0L);
			GL.setCapabilities(null);
			closeFuture.complete(null);
		}

		public void bindContext() {
			try {
				CompletableFuture<Void> f = later(() -> {
					hasContext.set(false);
					glfwMakeContextCurrent(0);
					GL.setCapabilities(null);
				});
				f.get();
				glfwMakeContextCurrent(id.get());
				GL.createCapabilities();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			} catch (ExecutionException ex) {
				ex.printStackTrace();
			}
		}

		public void releaseContext() {
			try {
				glfwMakeContextCurrent(0);
				GL.setCapabilities(null);
				CompletableFuture<Void> f = later(() -> {
					glfwMakeContextCurrent(id.get());
					GL.createCapabilities();
					hasContext.set(true);
				});
				hasContextLock.lock();
				hasContextCondition.signalAll();
				hasContextLock.unlock();
				f.get();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			} catch (ExecutionException ex) {
				ex.printStackTrace();
			}
		}

		public void scheduleDraw() {
			shouldDrawLock.lock();
			shouldDraw.set(true);
			shouldDrawCondition.signalAll();
			shouldDrawLock.unlock();
		}

		private FrameRenderer lastFrameRenderer = null;

		private void frame() {
			shouldDrawLock.lock();
			shouldDraw.set(false);
			shouldDrawLock.unlock();

			FrameRenderer fr = frameRenderer.get();
			if (fr != null) {
				if (lastFrameRenderer != fr) {
					if (lastFrameRenderer != null) {
						try {
							lastFrameRenderer.cleanup(LWJGLWindow.this);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					lastFrameRenderer = fr;
					if (fr != null) {
						try {
							fr.init(LWJGLWindow.this);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}

				if (viewportChanged.compareAndSet(true, false)) {
					glViewport(0, 0, getFramebuffer().width().intValue(), getFramebuffer().height().intValue());
					try {
						fr.windowSizeChanged(LWJGLWindow.this);
					} catch (GameException ex) {
						ex.printStackTrace();
					}
				}

				try {
					fr.renderFrame(LWJGLWindow.this);
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
				RenderMode mode = renderMode.get();
				if (mode == RenderMode.CONTINUOUSLY) {
					return true;
				}
				return shouldDraw.get();
			} finally {
				shouldDrawLock.unlock();
			}
		}

		private void workQueue() {
			if (renderThreadFutures.isEmpty()) {
				return;
			}
			Future f;
			while ((f = renderThreadFutures.poll()) != null) {
				f.r.run();
				f.f.complete(null);
			}
		}

		public CompletableFuture<Void> later(Runnable r) {
			CompletableFuture<Void> f = new CompletableFuture<>();
			renderThreadFutures.offer(new Future(f, r));
			shouldDrawLock.lock();
			shouldDrawCondition.signalAll();
			shouldDrawLock.unlock();
			return f;
		}

		@Override
		public Window getWindow() {
			return LWJGLWindow.this;
		}

		@Override
		public CompletableFuture<Void> runLater(GameRunnable runnable) {
			return runLater(() -> {
				runnable.run();
				return null;
			});
		}

		@Override
		public <T> CompletableFuture<T> runLater(GameCallable<T> callable) {
			CompletableFuture<T> f = new CompletableFuture<>();
			later(() -> {
				T t = null;
				try {
					t = callable.call();
				} catch (GameException ex) {
					ex.printStackTrace();
				}
				f.complete(t);
			});
			return f;
		}
	}

	private class WindowThread extends Thread {

		private final AtomicBoolean close = new AtomicBoolean(false);
		private final CompletableFuture<Void> closeFuture = new CompletableFuture<>();

		@Override
		public void run() {
			glfwDefaultWindowHints();
			glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
			glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE);
			long id = glfwCreateWindow(width.intValue(), height.intValue(), title.get(), 0, 0);
			if (id == NULL) {
				System.err.println("Failed to create GLFW Window");
				System.exit(-1);
				return;
			}
			glfwSetWindowSizeLimits(id, 10, 10, GLFW_DONT_CARE, GLFW_DONT_CARE);
			int[] a0 = new int[1];
			int[] a1 = new int[1];
			glfwGetWindowPos(id, a0, a1);
			LWJGLWindow.this.id.set(id);
			x.set(a0[0]);
			y.set(a1[0]);
			glfwGetFramebufferSize(id, a0, a1);
			framebuffer.width().setNumber(a0[0]);
			framebuffer.height().setNumber(a0[0]);
//			framebufferWidth.setNumber(a0[0]);
//			framebufferHeight.setNumber(a1[0]);

			windowThreadCreateFuture.get().complete(this);

			glfwSetScrollCallback(id, new GLFWScrollCallbackI() {
				@Override
				public void invoke(long window, double xoffset, double yoffset) {
					try {
						input.scroll((float) xoffset, (float) yoffset);
					} catch (GameException ex) {
						ex.printStackTrace();
					}
				}
			});
			glfwSetWindowCloseCallback(id, new GLFWWindowCloseCallbackI() {
				@Override
				public void invoke(long window) {
					try {
						closeCallback.get().close();
					} catch (GameException ex) {
						ex.printStackTrace();
					}
				}
			});
			glfwSetCursorEnterCallback(id, new GLFWCursorEnterCallbackI() {
				@Override
				public void invoke(long window, boolean entered) {
					mouse.setInWindow(entered);
				}
			});
			glfwSetCursorPosCallback(id, new GLFWCursorPosCallbackI() {
				@Override
				public void invoke(long window, double xpos, double ypos) {
					float omx = (float) mouse.getX();
					float omy = (float) mouse.getY();
					mouse.setPosition(xpos, ypos);
					input.mouseMove(omx, omy, (float) xpos, (float) ypos);
				}
			});
			glfwSetWindowSizeCallback(id, new GLFWWindowSizeCallbackI() {
				@Override
				public void invoke(long window, int w, int h) {
					width.setNumber(w);
					height.setNumber(h);
				}
			});
			glfwSetWindowPosCallback(id, new GLFWWindowPosCallbackI() {
				@Override
				public void invoke(long window, int xpos, int ypos) {
					x.set(xpos);
					y.set(ypos);
				}
			});
			glfwSetMouseButtonCallback(id, new GLFWMouseButtonCallbackI() {
				@Override
				public void invoke(long window, int button, int action, int mods) {
					switch (action) {
					case GLFW_PRESS:
						input.mousePress(button, (float) mouse.getX(), (float) mouse.getY());
						break;
					case GLFW_RELEASE:
						input.mouseRelease(button, (float) mouse.getX(), (float) mouse.getY());
						break;
					default:
						break;
					}
				}
			});
			glfwSetKeyCallback(id, new GLFWKeyCallbackI() {
				@Override
				public void invoke(long window, int key, int scancode, int action, int mods) {
					switch (action) {
					case GLFW_PRESS:
						input.keyPress(key, scancode, (char) 0);
						break;
					case GLFW_RELEASE:
						input.keyRelease(key, scancode, (char) 0);
						break;
					case GLFW_REPEAT:
						input.keyRepeat(key, scancode, (char) 0);
						break;
					default:
						break;
					}
				}
			});
			glfwSetCharCallback(id, new GLFWCharCallbackI() {
				@Override
				public void invoke(long window, int codepoint) {
					char ch = (char) codepoint;
					input.character(ch);
				}
			});
			glfwSetFramebufferSizeCallback(id, new GLFWFramebufferSizeCallbackI() {
				@Override
				public void invoke(long window, int width, int height) {
//					framebufferWidth.setNumber(width);
//					framebufferHeight.setNumber(height);
					framebuffer.width().setNumber(width);
					framebuffer.height().setNumber(height);
					logger.debugf("Viewport changed: (%4d, %4d)", width, height);
					LRenderThread rt = renderThread;
					rt.viewportChanged.set(true);
					if (renderMode.get() != RenderMode.MANUAL) {
						rt.bindContext();
						rt.frame();
						rt.releaseContext();
					}
				}
			});

			while (!close.get()) {
				glfwWaitEventsTimeout(1.0);
				glfwPollEvents();
				if (windowThreadFutures.isEmpty()) {
					continue;
				}
				Future f;
				while ((f = windowThreadFutures.poll()) != null) {
					try {
						f.r.run();
					} catch (Throwable e) {
						e.printStackTrace();
					}
					f.f.complete(null);
				}
			}
			stopRendering();
			glfwMakeContextCurrent(id);
			GL.createCapabilities();
			LWJGLWindow.this.closeFuture.complete(null);

			glfwDestroyWindow(id);
			closeFuture.complete(null);
		}

		public CompletableFuture<Void> later(Runnable r) {
			CompletableFuture<Void> f = new CompletableFuture<>();
			windowThreadFutures.offer(new Future(f, r));
			glfwPostEmptyEvent();
			return f;
		}
	}

	/**
	 * @author DasBabyPixel
	 */
	public static interface CloseCallback {
		/**
		 * Closes
		 * 
		 * @throws GameException
		 */
		void close() throws GameException;
	}

	private static class Future {
		private final CompletableFuture<Void> f;
		private final Runnable r;

		public Future(CompletableFuture<Void> f, Runnable r) {
			super();
			this.f = f;
			this.r = r;
		}
	}

	static {
		glfwInit();
	}

	@Override
	public RenderThread getRenderThread() {
		return renderThread;
	}

}
