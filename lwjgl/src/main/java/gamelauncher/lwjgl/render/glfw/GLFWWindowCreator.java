package gamelauncher.lwjgl.render.glfw;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengles.GLES20.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.util.concurrent.Phaser;
import java.util.function.Consumer;

import org.lwjgl.glfw.GLFWCharCallbackI;
import org.lwjgl.glfw.GLFWCursorEnterCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.lwjgl.glfw.GLFWWindowCloseCallbackI;
import org.lwjgl.glfw.GLFWWindowPosCallbackI;
import org.lwjgl.glfw.GLFWWindowRefreshCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.ContextProvider.ContextType;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.EmptyCamera;
import gamelauncher.engine.render.FrameCounter;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.util.GameException;
<<<<<<< HEAD
import gamelauncher.engine.util.concurrent.AbstractExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
=======
>>>>>>> branch 'master' of https://github.com/DasBabyPixel/GameLauncher.git
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.lwjgl.render.GlContext;
import gamelauncher.lwjgl.render.LWJGLGameRenderer.Entry;
import gamelauncher.lwjgl.render.framebuffer.BasicFramebuffer;
import gamelauncher.lwjgl.render.framebuffer.LWJGLFramebuffer;
import gamelauncher.lwjgl.render.states.GlStates;
import gamelauncher.lwjgl.render.states.StateRegistry;

@SuppressWarnings("javadoc")
public class GLFWWindowCreator implements GameRunnable {

	private final GLFWWindow window;

<<<<<<< HEAD
	private final GLFWWindowContext windowContext;

	public final GLFWWindowCreator.WindowContextRender contextRender;

=======
>>>>>>> branch 'master' of https://github.com/DasBabyPixel/GameLauncher.git
	public GLFWWindowCreator(GLFWWindow window) {
		this.window = window;
<<<<<<< HEAD
		this.windowContext = new GLFWWindowContext(window, this);
		this.contextRender = new WindowContextRender(window, window.getLauncher().getGlThreadGroup(), windowContext);
		this.contextRender.start();
=======
>>>>>>> branch 'master' of https://github.com/DasBabyPixel/GameLauncher.git
	}

	@Override
	public void run() {
		GLFWErrorCallback.createPrint().set();
		this.window.context.create();
		window.addContext(window.context);
		long id = this.window.context.getGLFWId();
		glfwSetWindowSize(id, window.width.intValue(), window.height.intValue());
		glfwSetWindowTitle(id, GameLauncher.NAME);
		if (id == NULL) {
			glfwTerminate();
			System.err.println("Failed to create GLFW Window");
			int error = glfwGetError(null);
			System.out.println(Integer.toHexString(error));
			System.exit(-1);
			return;
		}
		window.glfwId = id;

		glfwSetWindowSizeLimits(id, 10, 10, GLFW_DONT_CARE, GLFW_DONT_CARE);
		int[] a0 = new int[1];
		int[] a1 = new int[1];
		glfwGetWindowPos(id, a0, a1);
		window.x.setNumber(a0[0]);
		window.y.setNumber(a1[0]);
		glfwGetFramebufferSize(id, a0, a1);
		window.windowFramebuffer.width().setNumber(a0[0]);
		window.windowFramebuffer.height().setNumber(a0[0]);
		window.manualFramebuffer.query();
		window.windowRenderer.start();

		window.windowCloseFuture().thenRun(()->window.windowRenderer.exit());
		window.windowCreateFuture().complete(null);

		glfwSetScrollCallback(id, new GLFWScrollCallbackI() {

			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				try {
					GLFWWindowCreator.this.window.getInput().scroll((float) xoffset, (float) yoffset);
				} catch (GameException ex) {
					ex.printStackTrace();
				}
			}

		});
		glfwSetWindowCloseCallback(id, new GLFWWindowCloseCallbackI() {

			@Override
			public void invoke(long window) {
				try {
					GLFWWindowCreator.this.window.getCloseCallback().close();
				} catch (GameException ex) {
					ex.printStackTrace();
				}
			}

		});
		glfwSetCursorEnterCallback(id, new GLFWCursorEnterCallbackI() {

			@Override
			public void invoke(long window, boolean entered) {
				GLFWWindowCreator.this.window.getMouse().setInWindow(entered);
			}

		});
		glfwSetCursorPosCallback(id, new GLFWCursorPosCallbackI() {

			@Override
			public void invoke(long window, double xpos, double ypos) {
				// we are in the middle of the pixel. Important for guis later on so the
				// rounding works fine
				ypos = ypos + 0.5F;
				xpos = xpos + 0.5F;
				float omx = (float) GLFWWindowCreator.this.window.getMouse().getX();
				float omy = (float) GLFWWindowCreator.this.window.getMouse().getY();
				ypos = GLFWWindowCreator.this.window.height.doubleValue() - ypos;
				GLFWWindowCreator.this.window.getMouse().setPosition(xpos, ypos);
				GLFWWindowCreator.this.window.getInput().mouseMove(omx, omy, (float) xpos, (float) ypos);
			}

		});
		glfwSetWindowSizeCallback(id, new GLFWWindowSizeCallbackI() {

			@Override
			public void invoke(long window, int w, int h) {
				GLFWWindowCreator.this.window.width.setNumber(w);
				GLFWWindowCreator.this.window.height.setNumber(h);
			}

		});
		glfwSetWindowPosCallback(id, new GLFWWindowPosCallbackI() {

			@Override
			public void invoke(long window, int xpos, int ypos) {
				GLFWWindowCreator.this.window.x.setNumber(xpos);
				GLFWWindowCreator.this.window.y.setNumber(ypos);
			}

		});
		glfwSetMouseButtonCallback(id, new GLFWMouseButtonCallbackI() {

			@Override
			public void invoke(long window, int button, int action, int mods) {
				switch (action) {
				case GLFW_PRESS:
					GLFWWindowCreator.this.window.getInput()
							.mousePress(button, (float) GLFWWindowCreator.this.window.getMouse().getX(),
									(float) GLFWWindowCreator.this.window.getMouse().getY());
					break;
				case GLFW_RELEASE:
					GLFWWindowCreator.this.window.getInput()
							.mouseRelease(button, (float) GLFWWindowCreator.this.window.getMouse().getX(),
									(float) GLFWWindowCreator.this.window.getMouse().getY());
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
					GLFWWindowCreator.this.window.getInput().keyPress(key, scancode, (char) 0);
					break;
				case GLFW_RELEASE:
					GLFWWindowCreator.this.window.getInput().keyRelease(key, scancode, (char) 0);
					break;
				case GLFW_REPEAT:
					GLFWWindowCreator.this.window.getInput().keyRepeat(key, scancode, (char) 0);
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
				GLFWWindowCreator.this.window.getInput().character(ch);
			}

		});
		glfwSetWindowRefreshCallback(id, new GLFWWindowRefreshCallbackI() {

			@Override
			public void invoke(long window) {
				GLFWWindowCreator.this.window.getRenderThread().refresh();
			}

		});
		glfwSetFramebufferSizeCallback(id, new GLFWFramebufferSizeCallbackI() {

			@Override
			public void invoke(long w, int width, int height) {
				GLFWWindowCreator.this.window.logger.debugf("Viewport changed: (%4d, %4d)", width, height);
				GLFWRenderThread rt = GLFWWindowCreator.this.window.getRenderThread();
				window.windowFramebuffer.width().setNumber(width);
				window.windowFramebuffer.height().setNumber(height);
				rt.viewportChanged();
				if (GLFWWindowCreator.this.window.getRenderMode() != RenderMode.MANUAL) {
					rt.resize();
				}
			}

		});
	}

<<<<<<< HEAD
	public static class WindowContextRender extends AbstractExecutorThread {

		private final GLFWWindowContext context;

		private volatile boolean shouldDraw = false;

		private volatile boolean refreshAfterDraw = false;

		private volatile boolean refresh = false;

//		private volatile long lastResizeRefresh = 0;
		//
//		private volatile long lastActualFrame = 0;

		final FrameCounter frameCounter;

		final Phaser drawPhaser = new Phaser();

		final GLFWWindow window;

		BasicFBCopy basicFBCopy;

		private boolean forceTryRender = false;

		private Entry entry;

		private DrawContext dcontext;

		private final Consumer<Long> nanoSleeper = nanos -> {
//			this.waitForSignalTimeout(nanos);
		};

		public WindowContextRender(GLFWWindow window, ThreadGroup group, GLFWWindowContext context) {
			super(group);
			this.window = window;
			this.context = context;
			this.frameCounter = new FrameCounter();
			setName("GLFW-WindowRenderThread");
		}

		@Override
		protected void startExecuting() {
			context.makeCurrent();
			try {
				Threads.waitFor(window.windowCreateFuture());
			} catch (GameException ex) {
				ex.printStackTrace();
			}
			drawPhaser.register();
			// Init
			GlContext glContext = new GlContext();
			glContext.depth.enabled.value.set(true);
			glContext.depth.depthFunc.set(GL_LEQUAL);
			glContext.blend.enabled.value.set(true);
			glContext.blend.srcrgb.set(GL_SRC_ALPHA);
			glContext.blend.dstrgb.set(GL_ONE_MINUS_SRC_ALPHA);
			glContext.replace(null);
		}

		@Override
		protected void workExecution() {

			if (entry == null) {
				try {
					entry = window.getLauncher().getGameRenderer().getEntry(window);
					if (entry == null)
						return;
					basicFBCopy = new BasicFBCopy(window, entry.mainFramebuffer);
					dcontext = window.getLauncher().getContextProvider().loadContext(basicFBCopy, ContextType.HUD);
				} catch (GameException ex) {
					ex.printStackTrace();
				}
			}

			if (shouldDraw || window.getRenderMode() == RenderMode.CONTINUOUSLY) {
				shouldDraw = false;
				forceTryRender = false;
				refresh = false;
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
		protected void stopExecuting() {
			// TODO: Cleanup render
			try {
				window.getLauncher().getContextProvider().freeContext(dcontext, ContextType.HUD);
			} catch (GameException ex) {
				ex.printStackTrace();
			}

			window.getLauncher().getGlThreadGroup().terminated(this);
			try {
				StateRegistry.removeContext(window.getGLFWId());
			} catch (GameException ex) {
				window.getLauncher().handleError(ex);
			}
			StateRegistry.setContextHoldingThread(window.getGLFWId(), null);
		}

		private void frame(Type type) {
			// TODO: Render

			try {
				dcontext.update(EmptyCamera.instance());
				dcontext.drawModel(entry.mainScreenItemModel);
				dcontext.getProgram().clearUniforms();
			} catch (GameException ex) {
				ex.printStackTrace();
			}

			drawPhaser.arrive();
			frameCounter.frame(nanoSleeper);
		}

		@Override
		protected boolean shouldWaitForSignal() {
			return !forceTryRender;
		}

		public void resize() {
			try {
				Threads.waitFor(submit(() -> {
					frame(Type.RENDER);
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

		private static enum Type {
			RENDER, REFRESH
		}

		public Window getWindow() {
			return window;
		}

		private static class BasicFBCopy extends LWJGLFramebuffer {

			private final BasicFramebuffer bfb;

			public BasicFBCopy(GLFWWindow window, BasicFramebuffer fb) throws GameException {
				super(window);
				this.bfb = fb;
				width().bind(bfb.width());
				height().bind(bfb.height());
				bind();

				GlStates c = GlStates.current();
				c.framebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D,
						fb.getColorTexture().getTextureId(), 0);
				checkComplete();
				unbind();
			}

			@Override
			protected void cleanup0() throws GameException {
				super.cleanup0();
				width().unbind();
				height().unbind();
			}

		}

	}

=======
>>>>>>> branch 'master' of https://github.com/DasBabyPixel/GameLauncher.git
}
