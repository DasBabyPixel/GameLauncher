package gamelauncher.lwjgl.render.glfw;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

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
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameRunnable;

@SuppressWarnings("javadoc")
public class GLFWWindowCreator implements GameRunnable {

	private final GLFWWindow window;

	public GLFWWindowCreator(GLFWWindow window) {
		this.window = window;
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

}
