package gamelauncher.lwjgl.render;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.util.Optional;
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
import java.util.function.UnaryOperator;

import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWWindowCloseCallbackI;
import org.lwjgl.glfw.GLFWWindowPosCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.opengl.GL;

import gamelauncher.engine.GameException;
import gamelauncher.engine.render.FrameCounter;
import gamelauncher.engine.render.FrameRenderer;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.render.Window;
import gamelauncher.lwjgl.render.LWJGLInput.DeviceType;

public class LWJGLWindow implements Window {

	public final AtomicLong id = new AtomicLong();
	public final AtomicInteger width = new AtomicInteger();
	public final AtomicInteger height = new AtomicInteger();
	public final AtomicInteger framebufferWidth = new AtomicInteger();
	public final AtomicInteger framebufferHeight = new AtomicInteger();
	public final AtomicInteger x = new AtomicInteger();
	public final AtomicInteger y = new AtomicInteger();
	public final AtomicReference<String> title = new AtomicReference<>();
	private final AtomicReference<FrameRenderer> frameRenderer = new AtomicReference<>(null);
	private final AtomicBoolean floating = new AtomicBoolean(false);
	private final AtomicBoolean decorated = new AtomicBoolean(true);
	private final AtomicReference<RenderMode> renderMode = new AtomicReference<>(RenderMode.CONTINUOUSLY);
	private final AtomicReference<WindowThread> windowThread = new AtomicReference<>(null);
	private final AtomicReference<CompletableFuture<WindowThread>> windowThreadCreateFuture = new AtomicReference<>(
			new CompletableFuture<>());
	private final AtomicBoolean startRenderer = new AtomicBoolean(false);
	private final AtomicReference<RenderThread> renderThread = new AtomicReference<>(null);
	private final Queue<Future> windowThreadFutures = new ConcurrentLinkedQueue<>();
	private final Queue<Future> renderThreadFutures = new ConcurrentLinkedQueue<>();
	private final LWJGLDrawContext context = new LWJGLDrawContext(this);
	private final LWJGLInput input = new LWJGLInput(this);
	private final LWJGLCamera camera = new LWJGLCamera();
	private final AtomicReference<CloseCallback> closeCallback = new AtomicReference<>(new CloseCallback() {
		@Override
		public void close() {
			windowThread.get().close.set(true);
		}
	});
	
	public LWJGLCamera getCamera() {
		return camera;
	}

	public LWJGLWindow(int width, int height, String title) {
		this.width.set(width);
		this.height.set(height);
		this.title.set(title);
	}

	@Override
	public void setFrameRenderer(FrameRenderer renderer) {
		this.frameRenderer.set(renderer);
	}

	@Override
	public LWJGLDrawContext getContext() {
		return context;
	}

	@Override
	public LWJGLInput getInput() {
		return input;
	}
	
	public CloseCallback getCloseCallback() {
		return closeCallback.get();
	}
	
	public void setCloseCallback(CloseCallback closeCallback) {
		this.closeCallback.set(closeCallback);
	}

	public CompletableFuture<Void> renderLater(Runnable runnable) {
		RenderThread thread = renderThread.get();
		if (thread != null) {
			return thread.later(runnable);
		}
		CompletableFuture<Void> f = new CompletableFuture<>();
		renderThreadFutures.offer(new Future(f, runnable));
		return f;
	}

	public CompletableFuture<Void> later(Runnable runnable) {
		WindowThread thread = windowThread.get();
		if (thread != null) {
			return thread.later(runnable);
		}
		CompletableFuture<Void> f = new CompletableFuture<>();
		windowThreadFutures.offer(new Future(f, runnable));
		return f;
	}

	public boolean isClosed() {
		WindowThread t = windowThread.get();
		if (t == null)
			return true;
		return t.closeFuture.isDone();
	}

	public boolean isFloating() {
		return floating.get();
	}

	@Override
	public void beginFrame() {
	}

	@Override
	public void endFrame() {
		glfwSwapBuffers(id.get());
	}

	public boolean isDecorated() {
		return decorated.get();
	}

	public void setDecorated(boolean decorated) {
		if (this.decorated.compareAndSet(!decorated, decorated)) {
			later(() -> glfwSetWindowAttrib(id.get(), GLFW_DECORATED, decorated ? GLFW_TRUE : GLFW_FALSE));
		}
	}

	public void setFloating(boolean floating) {
		if (this.floating.compareAndSet(!floating, floating)) {
			later(() -> glfwSetWindowAttrib(id.get(), GLFW_FLOATING, floating ? GLFW_TRUE : GLFW_FALSE));
		}
	}

	public void setPosition(int x, int y) {
		later(() -> glfwSetWindowPos(id.get(), x, y));
	}

	public void setSize(int w, int h) {
		later(() -> glfwSetWindowSize(id.get(), w, h));
	}

	public CompletableFuture<Void> show() {
		return later(() -> glfwShowWindow(id.get()));
	}

	public CompletableFuture<Void> hide() {
		return later(() -> glfwHideWindow(id.get()));
	}

	public RenderMode getRenderMode() {
		return renderMode.get();
	}

	public void setRenderMode(RenderMode mode) {
		renderMode.set(mode);
		RenderThread t = renderThread.get();
		if (t != null) {
			t.scheduleDraw();
		}
	}

	public void createWindow() {
		WindowThread thread;
		if ((thread = windowThread.get()) == null) {
			windowThread.set(thread = new WindowThread());
			thread.setName("WindowThread");
			thread.start();
			try {
				windowThreadCreateFuture.get().get();
				thread.setName("WindowThread-" + id.get());
				if (startRenderer.get()) {
					startRendering0();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	public void closeWindow() {
		stopRendering();
		WindowThread thread;
		if ((thread = windowThread.get()) != null) {
			thread.close.set(true);
			try {
				thread.closeFuture.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	public void startRendering() {
		startRenderer.set(true);
		if (windowThreadCreateFuture.get().isDone()) {
			startRendering0();
		}
	}

	private void startRendering0() {
		AtomicBoolean updated = new AtomicBoolean(false);
		RenderThread thread = renderThread.updateAndGet(new UnaryOperator<LWJGLWindow.RenderThread>() {
			@Override
			public RenderThread apply(RenderThread thread) {
				if (thread == null) {
					updated.set(true);
					return new RenderThread();
				}
				return thread;
			}
		});
		if (updated.get()) {
			thread.setName("RenderThread-" + id.get());
			thread.start();
		}
	}

	public void stopRendering() {
		startRenderer.set(false);
		RenderThread thread = renderThread.getAndSet(null);
		if (thread != null) {
			thread.close.set(true);
			thread.shouldDrawLock.lock();
			thread.shouldDrawCondition.signalAll();
			thread.shouldDrawLock.unlock();
			try {
				thread.closeFuture.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	public Optional<FrameCounter> getFrameCounter() {
		RenderThread rt = renderThread.get();
		if (rt != null) {
			return Optional.of(rt.frameCounter);
		}
		return Optional.empty();
	}

	public CompletableFuture<Void> setLimits(int minw, int minh, int maxw, int maxh) {
		return later(() -> glfwSetWindowSizeLimits(id.get(), minw, minh, maxw, maxh));
	}

	public void scheduleDraw() {
		RenderThread thread = renderThread.get();
		if (thread != null) {
			thread.scheduleDraw();
		}
	}

	private class RenderThread extends Thread {

		private final AtomicBoolean shouldDraw = new AtomicBoolean(false);
		private final Lock shouldDrawLock = new ReentrantLock(true);
		private final Condition shouldDrawCondition = shouldDrawLock.newCondition();
		private final AtomicBoolean close = new AtomicBoolean(false);
		private final AtomicBoolean viewportChanged = new AtomicBoolean(true);
		private final CompletableFuture<Void> closeFuture = new CompletableFuture<>();
		private final FrameCounter frameCounter = new FrameCounter();
		private final Phaser drawPhaser = new Phaser();
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
					lastFrameRenderer.close(LWJGLWindow.this);
				} catch (GameException ex) {
					ex.printStackTrace();
				}
				lastFrameRenderer = null;
			}
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
			if (viewportChanged.compareAndSet(true, false)) {
				glViewport(0, 0, framebufferWidth.get(), framebufferHeight.get());
			}
			FrameRenderer fr = frameRenderer.get();
			if (fr != null) {
				if (lastFrameRenderer != fr) {
					if (lastFrameRenderer != null) {
						try {
							lastFrameRenderer.close(LWJGLWindow.this);
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
				try {
					context.update(camera);
					fr.renderFrame(LWJGLWindow.this);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			drawPhaser.arrive();

			shouldDrawLock.unlock();
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
			if(renderThreadFutures.isEmpty()) {
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
	}

	private class WindowThread extends Thread {

		private final AtomicBoolean close = new AtomicBoolean(false);
		private final CompletableFuture<Void> closeFuture = new CompletableFuture<>();

		@Override
		public void run() {
			glfwDefaultWindowHints();
			glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
//			glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
//			glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
//			glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
//			glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
			glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE);
			long id = glfwCreateWindow(width.get(), height.get(), title.get(), 0, 0);
			int[] a0 = new int[1];
			int[] a1 = new int[1];
			glfwGetWindowPos(id, a0, a1);
			LWJGLWindow.this.id.set(id);
			x.set(a0[0]);
			y.set(a1[0]);
			glfwGetFramebufferSize(id, a0, a1);
			framebufferWidth.set(a0[0]);
			framebufferHeight.set(a1[0]);

			windowThreadCreateFuture.get().complete(this);

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
			glfwSetWindowSizeCallback(id, new GLFWWindowSizeCallbackI() {
				@Override
				public void invoke(long window, int w, int h) {
					width.set(w);
					height.set(h);
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
						input.press(button, DeviceType.MOUSE);
						break;
					case GLFW_RELEASE:
						input.release(button, DeviceType.MOUSE);
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
						input.press(key, DeviceType.KEYBOARD);
						break;
					case GLFW_RELEASE:
						input.release(key, DeviceType.KEYBOARD);
						break;
					case GLFW_REPEAT:
						input.repeat(key, DeviceType.KEYBOARD);
						break;
					default:
						break;
					}
				}
			});
			glfwSetFramebufferSizeCallback(id, new GLFWFramebufferSizeCallbackI() {
				@Override
				public void invoke(long window, int width, int height) {
					framebufferWidth.set(width);
					framebufferHeight.set(height);
					RenderThread rt = renderThread.get();
					if (rt != null) {
						rt.viewportChanged.set(true);
						if (renderMode.get() != RenderMode.MANUAL) {
							rt.bindContext();
							rt.frame();
							rt.releaseContext();
						}
					}
				}
			});

			while (!close.get()) {
				glfwWaitEventsTimeout(1.0);
				glfwPollEvents();
				if(windowThreadFutures.isEmpty()) {
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

	public static interface CloseCallback {
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
}
