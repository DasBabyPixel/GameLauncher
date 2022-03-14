package game.render;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.function.*;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

public class Window {

	public final AtomicLong id = new AtomicLong();
	public final AtomicInteger width = new AtomicInteger();
	public final AtomicInteger height = new AtomicInteger();
	public final AtomicInteger framebufferWidth = new AtomicInteger();
	public final AtomicInteger framebufferHeight = new AtomicInteger();
	public final AtomicInteger x = new AtomicInteger();
	public final AtomicInteger y = new AtomicInteger();
	public final AtomicReference<String> title = new AtomicReference<>();
	public final AtomicReference<FrameRenderer> frameRenderer = new AtomicReference<>(null);
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

	public Window(int width, int height, String title) {
		this.width.set(width);
		this.height.set(height);
		this.title.set(title);
	}

	public CompletableFuture<Void> renderLater(Runnable runnable) {
		RenderThread thread = renderThread.get();
		if (thread != null) {
			return thread.later(runnable);
		} else {
			CompletableFuture<Void> f = new CompletableFuture<>();
			renderThreadFutures.offer(new Future(f, runnable));
			return f;
		}
	}

	public CompletableFuture<Void> later(Runnable runnable) {
		WindowThread thread = windowThread.get();
		if (thread != null) {
			return thread.later(runnable);
		} else {
			CompletableFuture<Void> f = new CompletableFuture<>();
			windowThreadFutures.offer(new Future(f, runnable));
			return f;
		}
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
		RenderThread thread = renderThread.updateAndGet(new UnaryOperator<Window.RenderThread>() {
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
			thread.interrupt();
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
		private final AtomicReference<CountDownLatch> shouldDrawLatch = new AtomicReference<>(newLatch());
		private final AtomicReference<CompletableFuture<Void>> drawFuture = new AtomicReference<>(
				new CompletableFuture<>());
		private final AtomicBoolean close = new AtomicBoolean(false);
		private final AtomicBoolean viewportChanged = new AtomicBoolean(true);
		private final CompletableFuture<Void> closeFuture = new CompletableFuture<>();
		private final Lock lock = new ReentrantLock(true);
		private final FrameCounter frameCounter = new FrameCounter();

		@Override
		public void run() {
			try {
				windowThreadCreateFuture.get().get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			glfwMakeContextCurrent(id.get());
			GL.createCapabilities();
			frame();
			scheduleDraw();
			while (!close.get()) {
				workQueue();
				if (shouldDraw()) {
					frame();
				} else {
					waitFor(shouldDrawLatch.get());
				}
			}
			closeFuture.complete(null);
		}

		public void scheduleDraw() {
			shouldDraw.set(true);
			shouldDrawLatch.getAndSet(newLatch()).countDown();
		}

		private void frame() {
			lock.lock();
			shouldDraw.set(false);
			if (viewportChanged.compareAndSet(true, false)) {
				glViewport(0, 0, framebufferWidth.get(), framebufferHeight.get());
			}
			FrameRenderer fr = frameRenderer.get();
			if (fr != null) {
				fr.renderFrame(Window.this);
				drawFuture.getAndSet(new CompletableFuture<>()).complete(null);
			}
			lock.unlock();
			frameCounter.frame();
		}

		private boolean shouldDraw() {
			return shouldDraw.get() || renderMode.get() == RenderMode.CONTINUOUSLY;
		}

		private CountDownLatch newLatch() {
			return new CountDownLatch(1);
		}

		private void waitFor(CountDownLatch latch) {
			while (latch.getCount() != 0L) {
				try {
					latch.await();
				} catch (InterruptedException e) {
					if (close.get())
						return;
					e.printStackTrace();
				}
			}
		}

		private void workQueue() {
			Future f;
			while ((f = renderThreadFutures.poll()) != null) {
				f.r.run();
				f.f.complete(null);
			}
		}

		public CompletableFuture<Void> later(Runnable r) {
			CompletableFuture<Void> f = new CompletableFuture<>();
			renderThreadFutures.offer(new Future(f, r));
			glfwPostEmptyEvent();
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
			long id = glfwCreateWindow(width.get(), height.get(), title.get(), 0, 0);
			int[] xpos = new int[1];
			int[] ypos = new int[1];
			glfwGetWindowPos(id, xpos, ypos);
			Window.this.id.set(id);
			x.set(xpos[0]);
			y.set(ypos[0]);

			windowThreadCreateFuture.get().complete(this);

			glfwSetWindowCloseCallback(id, new GLFWWindowCloseCallbackI() {
				@Override
				public void invoke(long window) {
					close.set(true);
				}
			});
			glfwSetWindowSizeCallback(id, new GLFWWindowSizeCallbackI() {
				@Override
				public void invoke(long window, int w, int h) {
					width.set(w);
					height.set(h);
					System.out.printf("New Size: %s %s%n", w, h);
				}
			});
			glfwSetWindowPosCallback(id, new GLFWWindowPosCallbackI() {
				@Override
				public void invoke(long window, int xpos, int ypos) {
					x.set(xpos);
					y.set(ypos);
					System.out.printf("New Pos: %s %s%n", xpos, ypos);
				}
			});
			glfwSetKeyCallback(id, new GLFWKeyCallbackI() {
				@Override
				public void invoke(long window, int key, int scancode, int action, int mods) {
				}
			});
			glfwSetFramebufferSizeCallback(id, new GLFWFramebufferSizeCallbackI() {
				@Override
				public void invoke(long window, int width, int height) {
					framebufferWidth.set(width);
					framebufferHeight.set(height);
					System.out.printf("New FBSize: %s %s%n", width, height);
					try {
						RenderThread rt = renderThread.get();
						if (rt != null) {
							rt.viewportChanged.set(true);
							rt.scheduleDraw();
							rt.drawFuture.get().get();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
			});

			while (!close.get()) {
				glfwWaitEventsTimeout(1.0);
//				glfwFocusWindow(id);
//				glfwMaximizeWindow(id);
				glfwPollEvents();
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
