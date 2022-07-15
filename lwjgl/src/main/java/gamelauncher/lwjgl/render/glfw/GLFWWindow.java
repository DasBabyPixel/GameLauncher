package gamelauncher.lwjgl.render.glfw;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Deque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengles.GLES;
import org.lwjgl.system.Configuration;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.FrameCounter;
import gamelauncher.engine.render.FrameRenderer;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.input.LWJGLInput;
import gamelauncher.lwjgl.input.LWJGLMouse;
import gamelauncher.lwjgl.render.framebuffer.WindowFramebuffer;

/**
 * @author DasBabyPixel
 *
 */
public class GLFWWindow implements Window, GLFWUser {

	final Logger logger;

	private static final AtomicLong names = new AtomicLong(0);

	final AtomicLong id = new AtomicLong();
	final AtomicBoolean closed = new AtomicBoolean(false);
	final AtomicBoolean closing = new AtomicBoolean(false);
	final AtomicLong name = new AtomicLong();
	final NumberValue width = NumberValue.zero();
	final NumberValue height = NumberValue.zero();
	final AtomicInteger x = new AtomicInteger();
	final AtomicInteger y = new AtomicInteger();
	final AtomicReference<String> title = new AtomicReference<>();
	final LWJGLMouse mouse = new LWJGLMouse(this);
	final AtomicReference<FrameRenderer> frameRenderer = new AtomicReference<>(null);
	final AtomicBoolean floating = new AtomicBoolean(false);
	final AtomicBoolean decorated = new AtomicBoolean(true);
	final AtomicReference<RenderMode> renderMode = new AtomicReference<>(RenderMode.CONTINUOUSLY);
	final GLFWRenderThread renderThread = new GLFWRenderThread(this);
	final Deque<Future> renderThreadFutures = new ConcurrentLinkedDeque<>();
	final GLFWThread glfwThread;
	final Framebuffer framebuffer = new WindowFramebuffer();
	final LWJGLInput input;
	final CompletableFuture<Window> closeFuture = new CompletableFuture<>();
	final CompletableFuture<Void> windowCreateFuture = new CompletableFuture<>();
	final Phaser drawPhaser = new Phaser();
	final AtomicBoolean swapBuffers = new AtomicBoolean(false);
	final LWJGLGameLauncher launcher;
	final GLFWWindowCreator windowCreator = new GLFWWindowCreator(this);
	final AtomicReference<CloseCallback> closeCallback = new AtomicReference<>(new CloseCallback() {
		@Override
		public void close() {
			closeWindow();
		}
	});

	/**
	 * @param launcher
	 * @param width
	 * @param height
	 * @param title
	 */
	public GLFWWindow(LWJGLGameLauncher launcher, int width, int height, String title) {
		this.launcher = launcher;
		this.glfwThread = this.launcher.getGLFWThread();
		this.glfwThread.addUser(this);
		this.input = new LWJGLInput(this);
		this.width.setNumber(width);
		this.height.setNumber(height);
		this.title.set(title);
		this.name.set(names.incrementAndGet());
		this.logger = Logger.getLogger(getClass().getSimpleName() + "-" + name.get());
		this.renderThread.setName("RenderThread-" + name.get());
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
		return this.glfwThread.submit(() -> {
			GLFWRenderThread rt = renderThread;
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
			onGLFWThread(() -> glfwSetWindowAttrib(id.get(), GLFW_DECORATED, decorated ? GLFW_TRUE : GLFW_FALSE));
		}
	}

	/**
	 * Sets if this window is floating
	 * 
	 * @param floating
	 */
	public void setFloating(boolean floating) {
		if (this.floating.compareAndSet(!floating, floating)) {
			onGLFWThread(() -> glfwSetWindowAttrib(id.get(), GLFW_FLOATING, floating ? GLFW_TRUE : GLFW_FALSE));
		}
	}

	/**
	 * Sets the position of this window
	 * 
	 * @param x
	 * @param y
	 */
	public void setPosition(int x, int y) {
		onGLFWThread(() -> glfwSetWindowPos(id.get(), x, y));
	}

	/**
	 * Sets the size of this window
	 * 
	 * @param w
	 * @param h
	 */
	public void setSize(int w, int h) {
		onGLFWThread(() -> glfwSetWindowSize(id.get(), w, h));
	}

	/**
	 * @return a new context
	 */
	public CompletableFuture<GLFWSecondaryContext> createSecondaryContext() {
		return this.glfwThread.submit(() -> {
			GLFWSecondaryContext secondary = new GLFWSecondaryContext(this);
			return secondary;
		});
	}

	/**
	 * Shows this window
	 * 
	 * @return a completionFuture
	 */
	public CompletableFuture<Void> show() {
		return onGLFWThread(() -> glfwShowWindow(id.get()));
	}

	/**
	 * Focuses this window
	 * 
	 * @return a completionFuture
	 */
	public CompletableFuture<Void> forceFocus() {
		return onGLFWThread(() -> {
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
		return onGLFWThread(() -> glfwHideWindow(id.get()));
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
		GLFWRenderThread t = renderThread;
		t.scheduleDraw();
	}

	/**
	 * Creates the window
	 */
	public void createWindow() {
		this.glfwThread.submit(this.windowCreator);
		try {
			windowCreateFuture.get();
			startRendering0();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the window
	 * 
	 * @return the closeFuture
	 */
	public CompletableFuture<Window> closeWindow() {
		if (closing.compareAndSet(false, true)) {
			stopRendering().thenRun(() -> {
				glfwThread.submit(() -> {
					glfwThread.removeUser(this);
					glfwMakeContextCurrent(id.get());
					GLES.createCapabilities();
//				GL.createCapabilities();

					closeFuture.complete(null);
					Callbacks.glfwFreeCallbacks(id.get());
					glfwDestroyWindow(id.get());
				});
			});
		}
		return closeFuture;
	}

	private void startRendering0() {
		renderThread.start();
	}

	@Override
	public void destroy() {
		this.closeWindow();
	}

	@Override
	public CompletableFuture<Void> doneFuture() {
		return this.closeFuture.thenApply(w -> null);
	}

	/**
	 * Stops rendering
	 */
	private CompletableFuture<Void> stopRendering() {
		renderThread.close.set(true);
		renderThread.shouldDrawLock.lock();
		renderThread.shouldDrawCondition.signalAll();
		renderThread.shouldDrawLock.unlock();
		return renderThread.closeFuture;
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
		return onGLFWThread(() -> glfwSetWindowSizeLimits(id.get(), minw, minh, maxw, maxh));
	}

	@Override
	public void scheduleDraw() {
		renderThread.scheduleDraw();
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

	@Override
	public void setFrameRenderer(FrameRenderer renderer) {
		this.frameRenderer.set(renderer);
	}

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
		return onGLFWThread(() -> {
			glfwSetWindowTitle(id.get(), title);
			GLFWWindow.this.title.set(title);
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
	public CompletableFuture<Void> renderLater(GameRunnable runnable) {
		return renderThread.submit(runnable);
	}

	/**
	 * Runs this {@link Runnable} on the Window Thread
	 * 
	 * @param runnable
	 * @return a completionFuture
	 */
	public CompletableFuture<Void> onGLFWThread(GameRunnable runnable) {
		return this.glfwThread.submit(runnable);
	}

	/**
	 * @return if this window is closed
	 */
	public boolean isClosed() {
		return this.closed.get();
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
	public GLFWRenderThread getRenderThread() {
		return renderThread;
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

	static {
		Configuration.OPENGLES_EXPLICIT_INIT.set(true);
		Configuration.OPENGL_EXPLICIT_INIT.set(true);

		GL.create();
		GLES.create(GL.getFunctionProvider());
		GL.destroy();
	}

	static class Future {
		final CompletableFuture<Void> f;
		final GameRunnable r;

		public Future(CompletableFuture<Void> f, GameRunnable r) {
			super();
			this.f = f;
			this.r = r;
		}
	}

}
