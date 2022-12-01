package gamelauncher.lwjgl.render.glfw;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

import org.lwjgl.glfw.GLFW;

import de.dasbabypixel.api.property.NumberValue;
import de.dasbabypixel.api.property.Property;
import de.dasbabypixel.api.property.implementation.ObjectProperty;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.input.Input;
import gamelauncher.engine.render.Frame;
import gamelauncher.engine.render.FrameCounter;
import gamelauncher.engine.render.FrameRenderer;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameConsumer;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.input.LWJGLInput;
import gamelauncher.lwjgl.input.LWJGLMouse;
import gamelauncher.lwjgl.render.framebuffer.ManualQueryFramebuffer;

@SuppressWarnings("javadoc")
public class GLFWFrame extends AbstractGameResource implements Frame {

	private static final Logger logger = Logger.getLogger();

	private static final GameConsumer<Frame> simpleCCB = f -> {
		((GLFWFrame) f).close0();
	};

	final LWJGLGameLauncher launcher;

	final GLFWFrameRenderThread renderThread;

	final LWJGLInput input;

	final LWJGLMouse mouse;

	final CompletableFuture<Frame> closeFuture;

	final GLFWFrameFramebuffer framebuffer;

	final Property<RenderMode> renderMode;

	final Property<FrameRenderer> frameRenderer;

	final FrameCounter frameCounter;

	final GLFWGLContext context;

	final Collection<GLFWGLContext> contexts;

	final Property<GameConsumer<Frame>> closeCallback = ObjectProperty.withValue(GLFWFrame.simpleCCB);

	final NumberValue windowWidth = NumberValue.zero();

	final NumberValue windowHeight = NumberValue.zero();

	final ManualQueryFramebuffer manualFramebuffer;

	private boolean created = false;

	public GLFWFrame(LWJGLGameLauncher launcher) throws GameException {
		super();
		this.launcher = launcher;
		this.contexts = Collections.synchronizedCollection(new HashSet<>());
		this.frameCounter = new FrameCounter();
		this.closeFuture = new CompletableFuture<>();
		this.renderMode = ObjectProperty.empty();
		this.frameRenderer = ObjectProperty.empty();
		this.mouse = new LWJGLMouse(this);
		this.framebuffer = new GLFWFrameFramebuffer(this);
		this.renderThread = new GLFWFrameRenderThread(this);
		this.manualFramebuffer = new ManualQueryFramebuffer(this.framebuffer);
		this.input = new LWJGLInput(this);
		this.context = new GLFWGLContext(Collections.synchronizedCollection(new HashSet<>()));
		Threads.waitFor(this.launcher.getGLFWThread().submit(() -> this.context.create(this)));
		this.renderThread.start();
		this.created = true;
	}

	public GLFWFrame(LWJGLGameLauncher launcher, GLFWGLContext context) {
		super();
		this.launcher = launcher;
		this.contexts = Collections.synchronizedCollection(new HashSet<>());
		this.frameCounter = new FrameCounter();
		this.closeFuture = new CompletableFuture<>();
		this.renderMode = ObjectProperty.empty();
		this.frameRenderer = ObjectProperty.empty();
		this.mouse = new LWJGLMouse(this);
		this.framebuffer = new GLFWFrameFramebuffer(this);
		this.renderThread = new GLFWFrameRenderThread(this);
		this.manualFramebuffer = new ManualQueryFramebuffer(this.framebuffer);
		this.input = new LWJGLInput(this);
		this.context = context;
		this.renderThread.start();
		this.created = true;
	}

	/**
	 * Called from the default closecallback
	 */
	private void close0() {
		new Thread(() -> {
			try {
				this.cleanup();
			} catch (GameException ex) {
				ex.printStackTrace();
			}
		});
	}

	@Override
	protected void cleanup0() throws GameException {
		this.renderThread.cleanupContextOnExit = true;
		Threads.waitFor(this.renderThread.exit());
		(this.context.parent != null ? this.context.parent : this).freeContextManual(this.context, true);
		for (GLFWGLContext context : this.contexts) {
			context.parent.freeContext(context);
		}
		this.manualFramebuffer.cleanup();
		this.framebuffer.cleanup();
		this.contexts.clear();
	}

	@Override
	public void scheduleDraw() {
		this.renderThread.scheduleDraw();
	}

	@Override
	public void waitForFrame() {
		this.renderThread.waitForFrame();
	}

	@Override
	public void scheduleDrawWaitForFrame() {
		this.renderThread.scheduleDrawWait();
	}

	public GLFWFrameRenderThread renderThread() {
		return this.renderThread;
	}

	public CompletableFuture<Void> showWindow() {
		return this.launcher.getGLFWThread().submit(() -> {
			this.framebuffer.swapBuffers().setValue(true);
			GLFW.glfwShowWindow(this.context.glfwId);
			this.renderThread.scheduleDrawRefreshWait();
		});
	}

	public CompletableFuture<Void> hideWindow() {
		return this.launcher.getGLFWThread().submit(() -> {
			this.framebuffer.swapBuffers().setValue(false);
			GLFW.glfwHideWindow(this.context.glfwId);
		});
	}

	public void freeContext(GLFWGLContext context) throws GameException {
		if (!this.contexts.contains(context)) {
			throw new IllegalStateException("Frame does not contain context");
		}
		this.freeContextManual(context, false);
		this.contexts.remove(context);
	}

	public void freeContextManual(GLFWGLContext context, boolean renderThreadCleanedUp) throws GameException {
		if (!renderThreadCleanedUp) {
			context.cleanup();
		}
	}

	public GLFWGLContext newContext() throws GameException {
		return this.newContext(false);
	}

	public GLFWGLContext newContext(boolean manual) throws GameException {
		GLFWGLContext ctx = new GLFWGLContext(this.context.sharedContexts);
		ctx.parent = this;
		for (GLFWGLContext c : ctx.sharedContexts) {
			c.beginCreationShared();
		}
		Threads.waitFor(this.launcher.getGLFWThread().submit(() -> {
			ctx.create(this);
		}));
		for (GLFWGLContext c : ctx.sharedContexts) {
			c.endCreationShared();
		}
		if (!manual)
			this.contexts.add(ctx);
		return ctx;
	}

	public long getGLFWId() {
		return this.context.glfwId;
	}

	@Override
	public GLFWFrame newFrame() throws GameException {
		GLFWGLContext ctx = this.newContext(true);
		GLFWFrame frame = new GLFWFrame(this.launcher, ctx);
		return frame;
	}

	@Override
	public Input getInput() {
		return this.input;
	}

	@Override
	public CompletableFuture<Frame> frameCloseFuture() {
		return this.closeFuture;
	}

	@Override
	public ManualQueryFramebuffer framebuffer() {
		return this.manualFramebuffer;
	}

	@Override
	public RenderMode renderMode() {
		return this.renderMode.getValue();
	}

	@Override
	public void renderMode(RenderMode renderMode) {
		this.renderMode.setValue(renderMode);
	}

	@Override
	public void frameRenderer(FrameRenderer renderer) {
		this.frameRenderer.setValue(renderer);
	}

	@Override
	public FrameRenderer frameRenderer() {
		return this.frameRenderer.getValue();
	}

	@Override
	public FrameCounter frameCounter() {
		return this.frameCounter;
	}

	public LWJGLMouse getMouse() {
		return this.mouse;
	}

	@Override
	public LWJGLGameLauncher getLauncher() {
		return this.launcher;
	}

	public Property<GameConsumer<Frame>> closeCallback() {
		return this.closeCallback;
	}

	static class Creator implements Runnable {

		public GLFWFrame frame;

		public long glfwId;

		public final NumberValue width = NumberValue.withValue(1600 / 2);

		public final NumberValue height = NumberValue.withValue(900 / 2);

		public final NumberValue fbwidth = NumberValue.zero();

		public final NumberValue fbheight = NumberValue.zero();

		public final NumberValue scaleX = NumberValue.zero();

		public final NumberValue scaleY = NumberValue.zero();

		public Creator(GLFWFrame frame) {
			this.frame = frame;
		}

		@Override
		public void run() {
			GLFW.glfwDefaultWindowHints();
			GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_OPENGL_ES_API);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
			GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_DEBUG, GLFW.GLFW_TRUE);
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
			GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, GLFW.GLFW_TRUE);

			this.glfwId = GLFW.glfwCreateWindow(this.width.intValue(), this.height.intValue(), GameLauncher.NAME, 0,
					this.frame.context.getGLFWId());
			GLFW.glfwSetWindowSizeLimits(this.glfwId, 1, 1, GLFW.GLFW_DONT_CARE, GLFW.GLFW_DONT_CARE);
			int[] a0 = new int[1];
			int[] a1 = new int[1];
			GLFW.glfwGetWindowSize(this.glfwId, a0, a1);
			this.width.setNumber(a0[0]);
			this.height.setNumber(a1[0]);
			if (!this.frame.created) {
				this.frame.windowWidth.bind(this.width);
				this.frame.windowHeight.bind(this.height);
			}
			GLFW.glfwGetFramebufferSize(this.glfwId, a0, a1);
			this.fbwidth.setNumber(a0[0]);
			this.fbheight.setNumber(a1[0]);
			if (!this.frame.created) {
				this.frame.framebuffer.width().bind(this.fbwidth);
				this.frame.framebuffer.height().bind(this.fbheight);
			}
			float[] f0 = new float[1];
			float[] f1 = new float[1];
			GLFW.glfwGetWindowContentScale(this.glfwId, f0, f1);
			this.scaleX.setNumber(f0[0]);
			this.scaleY.setNumber(f1[0]);
			GLFW.glfwSetScrollCallback(this.glfwId, (wid, xo, yo) -> {
				try {
					this.frame.input.scroll((float) xo, (float) yo);
				} catch (GameException ex) {
					ex.printStackTrace();
				}
			});
			GLFW.glfwSetWindowCloseCallback(this.glfwId, wid -> {
				GameConsumer<Frame> cs = this.frame.closeCallback.getValue();
				if (cs != null) {
					try {
						cs.accept(this.frame);
					} catch (GameException ex) {
						ex.printStackTrace();
					}
				}
			});
			GLFW.glfwSetCursorEnterCallback(this.glfwId, (wid, entered) -> {
				this.frame.mouse.setInWindow(entered);
			});
			GLFW.glfwSetCursorPosCallback(this.glfwId, (wid, xpos, ypos) -> {
				ypos = ypos + 0.5F;
				xpos = xpos + 0.5F;
				float omx = (float) this.frame.getMouse().getX();
				float omy = (float) this.frame.getMouse().getY();
				ypos = this.height.doubleValue() - ypos;
				this.frame.getMouse().setPosition(xpos, ypos);
				this.frame.input.mouseMove(omx, omy, (float) xpos, (float) ypos);
			});
			GLFW.glfwSetWindowSizeCallback(this.glfwId, (wid, w, h) -> {
				this.width.setNumber(w);
				this.height.setNumber(h);
			});
			GLFW.glfwSetMouseButtonCallback(this.glfwId, (wid, button, action, mods) -> {
				switch (action) {
				case GLFW_PRESS:
					this.frame.input.mousePress(button, (float) this.frame.getMouse().getX(),
							(float) this.frame.getMouse().getY());
					break;
				case GLFW_RELEASE:
					this.frame.input.mouseRelease(button, (float) this.frame.getMouse().getX(),
							(float) this.frame.getMouse().getY());
					break;
				default:
					break;
				}
			});
			GLFW.glfwSetKeyCallback(this.glfwId, (wid, key, scancode, action, mods) -> {
				switch (action) {
				case GLFW_PRESS:
					this.frame.input.keyPress(key, scancode, (char) 0);
					break;
				case GLFW_RELEASE:
					this.frame.input.keyRelease(key, scancode, (char) 0);
					break;
				case GLFW_REPEAT:
					this.frame.input.keyRepeat(key, scancode, (char) 0);
					break;
				default:
					break;
				}
			});
			GLFW.glfwSetWindowContentScaleCallback(this.glfwId, (window, xscale, yscale) -> {
				this.scaleX.setNumber(xscale);
				this.scaleY.setNumber(yscale);
			});
			GLFW.glfwSetCharCallback(this.glfwId, (wid, codepoint) -> {
				this.frame.input.character((char) codepoint);
			});
			GLFW.glfwSetWindowRefreshCallback(this.glfwId, wid -> {
				this.frame.renderThread.refreshWait();
			});
			GLFW.glfwSetFramebufferSizeCallback(this.glfwId, (wid, width, height) -> {
				GLFWFrame.logger.debugf("Viewport changed: (%4d, %4d)", width, height);
				GLFWFrameRenderThread rt = this.frame.renderThread;
				this.fbwidth.setNumber(width);
				this.fbheight.setNumber(height);
//				rt.viewportChanged();
				if (this.frame.renderMode() != RenderMode.MANUAL) {
					rt.scheduleDrawWait();
				}
			});
		}

	}

}