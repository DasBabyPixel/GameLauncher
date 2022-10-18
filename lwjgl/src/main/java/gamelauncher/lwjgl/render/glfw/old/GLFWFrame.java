package gamelauncher.lwjgl.render.glfw.old;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.glfw.GLFW;

import de.dasbabypixel.api.property.NumberValue;
import de.dasbabypixel.api.property.Property;
import de.dasbabypixel.api.property.implementation.ObjectProperty;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.input.Input;
import gamelauncher.engine.render.Frame;
import gamelauncher.engine.render.FrameCounter;
import gamelauncher.engine.render.FrameRenderer;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameConsumer;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.input.LWJGLInput;
import gamelauncher.lwjgl.input.LWJGLMouse;

@SuppressWarnings("javadoc")
public class GLFWFrame extends AbstractGameResource implements Frame {

	private static final Logger logger = Logger.getLogger();

	private static final GameConsumer<Frame> simpleCCB = f -> {
		((GLFWFrame) f).close0();
	};

	private final GLFWFrame parent;

	private final Property<RenderMode> renderMode = ObjectProperty.withValue(RenderMode.ON_UPDATE);

	private final Property<FrameRenderer> frameRenderer = ObjectProperty.empty();

	private final FrameCounter frameCounter;

	Framebuffer framebuffer;

	private final GLFWFrameRenderThread renderThread;

	private final CompletableFuture<Frame> closeFuture = new CompletableFuture<>();

	final LWJGLGameLauncher launcher;

	private final LWJGLInput input;

	public volatile boolean swapBuffers = false;

	protected final Collection<GLFWContext> contexts = new CopyOnWriteArrayList<>();

	private final LWJGLMouse mouse;

	final Property<GameConsumer<Frame>> closeCallback = ObjectProperty.withValue(GLFWFrame.simpleCCB);

	private final NumberValue width = NumberValue.zero();

	private final NumberValue height = NumberValue.zero();

	public GLFWFrame(GLFWFrame parent) throws GameException {
		this.parent = parent;
		this.renderThread = new GLFWFrameRenderThread(this);
		this.frameCounter = new FrameCounter();
		this.launcher = parent.launcher;
//		this.input = new LWJGLInput(this);
//		this.mouse = new LWJGLMouse(this);
		this.input = null;
		this.mouse = null;
		this.renderThread.start();
	}

	public GLFWFrame(LWJGLGameLauncher launcher) throws GameException {
		this.parent = null;
		this.launcher = launcher;
		this.renderThread = new GLFWFrameRenderThread(this);
		this.frameCounter = new FrameCounter();
//		this.input = new LWJGLInput(this);
//		this.mouse = new LWJGLMouse(this);
		this.input = null;
		this.mouse = null;
		this.renderThread.start();
	}

	public long getGLFWId() {
		return this.renderThread.context.getGLFWId();
	}

	public Property<GameConsumer<Frame>> closeCallback() {
		return this.closeCallback;
	}

	GLFWContext newContext(GLFWFrame original, boolean hasWindow) throws GameException {
		if (this.parent != null)
			return this.parent.newContext(original, hasWindow);
		GLFWContext ctx = new GLFWContext();
		for (GLFWContext context : this.contexts) {
			context.beginCreationShared();
		}
		Threads.waitFor(this.launcher.getGLFWThread().submit(() -> {
			Creator c = ctx.create(GLFWFrame.this);
			if (hasWindow) {
				System.out.println("bindWH: " + c.fbwidth.intValue() + " " + c.fbheight.intValue());
				original.width.bind(c.fbwidth);
				original.height.bind(c.fbheight);
			}
		}));
		this.contexts.add(ctx);
		for (GLFWContext context : this.contexts) {
			context.endCreationShared();
		}
		return ctx;
	}

	void freeContext(GLFWContext context) throws GameException {
		if (this.parent != null) {
			this.parent.freeContext(context);
			return;
		}
		context.cleanup();
		this.contexts.remove(context);
	}

	public CompletableFuture<Void> hide() {
		return this.launcher.getGLFWThread().submit(() -> {
			GLFW.glfwHideWindow(this.getGLFWId());
		});
	}

	@Override
	public void scheduleDraw() {
		this.renderThread.scheduleDraw();
		if (this.parent != null)
			this.parent.scheduleDraw();
	}

	public LWJGLMouse getMouse() {
		return this.mouse;
	}

	public void showEndFrame() throws GameException {
		Threads.waitFor(this.renderThread.createFuture);
		Threads.waitFor(this.launcher.getGLFWThread().submit(() -> GLFW.glfwShowWindow(this.getGLFWId())));
		this.framebuffer.beginFrame();
		this.framebuffer.endFrame();
	}

	@Override
	public void waitForFrame() {
		this.renderThread.waitForFrame();
	}

	@Override
	public void scheduleDrawWaitForFrame() {
		this.renderThread.scheduleDrawWaitForFrame();
		if (this.parent != null)
			this.parent.scheduleDrawWaitForFrame();
	}

	@Override
	protected void cleanup0() throws GameException {
		Threads.waitFor(this.renderThread.exit());
	}

	public CompletableFuture<Frame> close0() {
		this.renderThread.exit();
		return this.frameCloseFuture();
	}

	@Override
	public Framebuffer framebuffer() {
		return this.framebuffer;
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
	public GLFWFrame newFrame() throws GameException {
		return new GLFWFrame(this);
	}

	@Override
	public FrameCounter frameCounter() {
		return this.frameCounter;
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
	public LWJGLGameLauncher getLauncher() {
		return this.launcher;
	}

	@Override
	public Input getInput() {
		return this.input;
	}

	@Override
	public CompletableFuture<Frame> frameCloseFuture() {
		return this.closeFuture;
	}

//	public NumberValue width() {
//		return this.width;
//	}
//
//	public NumberValue height() {
//		return this.height;
//	}

	static class Creator implements Runnable {

		GLFWFrame frame = null;

		boolean transparentFb = false;

		long glfwId = 0;

		NumberValue width = NumberValue.withValue(400);

		NumberValue height = NumberValue.withValue(400);

		NumberValue fbwidth = NumberValue.zero();

		NumberValue fbheight = NumberValue.zero();

		@Override
		public void run() {
			GLFW.glfwDefaultWindowHints();
			GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_OPENGL_ES_API);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
			GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_DEBUG, GLFW.GLFW_TRUE);
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
			if (this.transparentFb) {
				GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, GLFW.GLFW_TRUE);
			}
			this.glfwId = GLFW.glfwCreateWindow(this.width.intValue(), this.height.intValue(), GameLauncher.NAME, 0, 0);
			GLFW.glfwSetWindowSizeLimits(this.glfwId, 1, 1, GLFW.GLFW_DONT_CARE, GLFW.GLFW_DONT_CARE);
			int[] a0 = new int[1];
			int[] a1 = new int[1];

			GLFW.glfwGetFramebufferSize(this.glfwId, a0, a1);
			this.fbwidth.setNumber(a0[0]);
			this.fbheight.setNumber(a1[0]);
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
				ypos = this.frame.height.doubleValue() - ypos;
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
			GLFW.glfwSetCharCallback(this.glfwId, (wid, codepoint) -> {
				this.frame.input.character((char) codepoint);
			});
			GLFW.glfwSetWindowRefreshCallback(this.glfwId, (wid) -> {
				this.frame.renderThread.refresh();
			});
			GLFW.glfwSetFramebufferSizeCallback(this.glfwId, (wid, width, height) -> {
				GLFWFrame.logger.debugf("Viewport changed: (%4d, %4d)", width, height);
				GLFWFrameRenderThread rt = this.frame.renderThread;
				this.frame.framebuffer.width().setNumber(width);
				this.frame.framebuffer.height().setNumber(height);
//				rt.viewportChanged();
				if (this.frame.renderMode() != RenderMode.MANUAL) {
					rt.resize();
				}
			});

		}

	}

}
