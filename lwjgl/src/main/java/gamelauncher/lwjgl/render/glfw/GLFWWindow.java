package gamelauncher.lwjgl.render.glfw;

import static org.lwjgl.glfw.GLFW.*;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

import de.dasbabypixel.api.property.NumberValue;
import de.dasbabypixel.api.property.Property;
import de.dasbabypixel.api.property.implementation.ObjectProperty;
import gamelauncher.engine.render.FrameCounter;
import gamelauncher.engine.render.FrameRenderer;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.input.LWJGLInput;
import gamelauncher.lwjgl.input.LWJGLMouse;
import gamelauncher.lwjgl.render.framebuffer.ManualQueryFramebuffer;
import gamelauncher.lwjgl.render.framebuffer.WindowFramebuffer;
import gamelauncher.lwjgl.render.states.StateRegistry;

@SuppressWarnings("javadoc")
public class GLFWWindow implements Window, GLFWUser {

	private final LWJGLGameLauncher launcher;

	final WindowFramebuffer windowFramebuffer;

	final ManualQueryFramebuffer manualFramebuffer;

	private final GLFWThread glfwThread;

	private final CompletableFuture<Void> destroyFuture;

	@Deprecated
	private final CompletableFuture<Window> windowCloseFuture;

	private final AtomicBoolean closing = new AtomicBoolean(false);

	private final CompletableFuture<Window> windowCreateFuture;

	private final Property<FrameRenderer> frameRenderer;

	private final Property<RenderMode> renderMode;

	private final LWJGLInput input;

	private final LWJGLMouse mouse;

	volatile long glfwId;

	private final GLFWRenderThread renderThread;

	private final GLFWSecondaryContext secondaryContext;

	final NumberValue x;

	final NumberValue y;

	final NumberValue width;

	final NumberValue height;

	final Property<String> title;

	private final Property<CloseCallback> closeCallback;

	final Logger logger;

	final FrameCounter frameCounter;

	private final AtomicBoolean swapBuffers = new AtomicBoolean(false);

	private final AtomicBoolean frameBegun = new AtomicBoolean(false);

	public GLFWWindow(LWJGLGameLauncher launcher, String title, int width, int height) {
		this.launcher = launcher;
		this.logger = Logger.getLogger();
		this.glfwId = 0;
		this.frameCounter = new FrameCounter();
		this.glfwThread = launcher.getGLFWThread();
		this.destroyFuture = new CompletableFuture<>();
		this.windowCloseFuture = this.destroyFuture.thenApply(v -> GLFWWindow.this);
		this.windowCreateFuture = new CompletableFuture<>();
		this.renderMode = ObjectProperty.empty();
		this.frameRenderer = ObjectProperty.empty();
		this.renderThread = new GLFWRenderThread(this);
		this.windowFramebuffer = new WindowFramebuffer(this);
		this.manualFramebuffer = new ManualQueryFramebuffer(this.windowFramebuffer);
		this.mouse = new LWJGLMouse(this);
		this.input = new LWJGLInput(this);
		this.secondaryContext = new GLFWSecondaryContext(this);
		this.x = NumberValue.zero();
		this.y = NumberValue.zero();
		this.title = ObjectProperty.withValue(title);
		this.width = NumberValue.withValue(width);
		this.height = NumberValue.withValue(height);
		this.closeCallback = ObjectProperty.withValue(() -> destroy());
		glfwThread.addUser(this);

	}

	public NumberValue width() {
		return width;
	}

	public NumberValue height() {
		return height;
	}

	public CloseCallback getCloseCallback() {
		return closeCallback.getValue();
	}

	public void setCloseCallback(CloseCallback callback) {
		this.closeCallback.setValue(callback);
	}

	public LWJGLMouse getMouse() {
		return mouse;
	}

	@Override
	public FrameCounter getFrameCounter() {
		return frameCounter;
	}

	public CompletableFuture<Void> createWindow() {
		return this.glfwThread.submit(new GLFWWindowCreator(this));
	}

	@Override
	public CompletableFuture<Void> destroy() {
		if (closing.compareAndSet(false, true)) {
			renderThread.submit(() -> {
				manualFramebuffer.cleanup();
			}).thenRun(() -> renderThread.exit().thenRun(() -> {
				glfwThread.submit(() -> {
					StateRegistry.removeWindow(glfwId);
					glfwDestroyWindow(glfwId);
					glfwId = 0;
					glfwThread.removeUser(this);
					destroyFuture().complete(null);
				});
			}));
		}
		return destroyFuture();
	}

	public CompletableFuture<Void> hide() {
		return glfwThread.submit(() -> {
			glfwHideWindow(glfwId);
		});
	}

	@Override
	public void beginFrame() {
		manualFramebuffer.query();
		if (!frameBegun.compareAndSet(false, true)) {
			throw new IllegalStateException("Already rendering frame");
		}
	}

	@Override
	public void endFrame() {
		boolean wasFrame = frameBegun.compareAndSet(true, false);
		if (swapBuffers.get() && wasFrame) {
			if (!manualFramebuffer.newValue().booleanValue()) {
				glfwSwapBuffers(this.getGLFWId());
			}
		}
	}

	public CompletableFuture<Void> showAndEndFrame() {
		CyclicBarrier barrier = new CyclicBarrier(2);
		CountDownLatch latch = new CountDownLatch(1);
		CompletableFuture<Void> fut = new CompletableFuture<>();
		this.glfwThread.submit(() -> {
			renderThread.submit(() -> {
				try {
					barrier.await();
					latch.await();
				} catch (InterruptedException | BrokenBarrierException ex) {
					ex.printStackTrace();
				}
				if (swapBuffers.compareAndSet(false, true)) {
					glfwSwapBuffers(getGLFWId());
				}
				fut.complete(null);
			});
			try {
				barrier.await();
			} catch (InterruptedException | BrokenBarrierException ex) {
				ex.printStackTrace();
			}
			glfwShowWindow(getGLFWId());
			latch.countDown();
		});
		return fut;
	}

	public GLFWSecondaryContext getSecondaryContext() {
		return secondaryContext;
	}

	@Override
	public void scheduleDraw() {
		this.renderThread.scheduleDraw();
	}

	@Override
	public void waitForFrame() {
		this.renderThread.drawPhaser.awaitAdvance(this.renderThread.drawPhaser.getPhase());
	}

	@Deprecated
	public void swapBuffers(boolean val) {
		this.swapBuffers.set(val);
	}

	@Override
	public void scheduleDrawAndWaitForFrame() {
		int phase = this.renderThread.drawPhaser.getPhase();
		this.renderThread.scheduleDraw();
		this.renderThread.drawPhaser.awaitAdvance(phase);
	}

	@Override
	public void setFrameRenderer(FrameRenderer renderer) {
		this.frameRenderer.setValue(renderer);
	}

	@Override
	public FrameRenderer getFrameRenderer() {
		return frameRenderer.getValue();
	}

	public long getGLFWId() {
		return glfwId;
	}

	@Override
	public RenderMode getRenderMode() {
		return this.renderMode.getValue();
	}

	@Override
	public void setRenderMode(RenderMode mode) {
		this.renderMode.setValue(mode);
	}

	@Override
	public LWJGLInput getInput() {
		return input;
	}

	@Override
	public GLFWRenderThread getRenderThread() {
		return renderThread;
	}

	public CompletableFuture<Window> windowCreateFuture() {
		return windowCreateFuture;
	}

	@Override
	public CompletableFuture<Window> windowCloseFuture() {
		return windowCloseFuture;
	}

	@Override
	public LWJGLGameLauncher getLauncher() {
		return launcher;
	}

	@Override
	@Deprecated
	public ManualQueryFramebuffer getFramebuffer() {
		return manualFramebuffer;
	}

	@Override
	public CompletableFuture<Void> destroyFuture() {
		return this.destroyFuture;
	}

	public static interface CloseCallback {

		void close() throws GameException;

	}

}
