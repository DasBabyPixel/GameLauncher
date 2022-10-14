package gamelauncher.lwjgl.render.glfw.old;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengles.GLES32;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.logging.LogLevel;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.render.glfw.GLUtil;
import gamelauncher.lwjgl.render.states.GlStates;
import gamelauncher.lwjgl.render.states.StateRegistry;

class GLFWContext extends AbstractGameResource {

	private static final Logger logger = Logger.getLogger();

	private static final LogLevel level = new LogLevel("GL", 10);

	private ExecutorThread owner;

	private boolean owned;

	private long glfwId;

	GLFWContext() {
	}

	public long getGLFWId() {
		return this.glfwId;
	}

	@Override
	protected void cleanup0() throws GameException {
		if (this.owned) {
			StateRegistry.removeContext(this.glfwId);
			this.destroyCurrent();
		}
		GLFW.glfwDestroyWindow(this.glfwId);
		StateRegistry.removeWindow(this.glfwId);
	}

	synchronized void beginCreationShared() throws GameException {
		if (this.owned) {
			Threads.waitFor(this.owner.submit(() -> {
				StateRegistry.setContextHoldingThread(this.glfwId, null);
			}));
		}
	}

	synchronized void endCreationShared() throws GameException {
		if (this.owned) {
			Threads.waitFor(this.owner.submit(() -> {
				StateRegistry.setContextHoldingThread(this.glfwId, Thread.currentThread());
			}));
		}
	}

	synchronized GLFWFrame.Creator create(GLFWFrame frame) {
		GLFWFrame.Creator creator = new GLFWFrame.Creator();
		creator.frame = frame;
		creator.run();
		this.glfwId = creator.glfwId;
		StateRegistry.addWindow(this.glfwId);
		return creator;
	}

	synchronized void destroyCurrent() {
		StateRegistry.setContextHoldingThread(this.glfwId, null);
		this.owned = false;
		this.owner = null;
	}

	synchronized void makeCurrent() {
		this.owned = true;
		this.owner = (ExecutorThread) Thread.currentThread();
		StateRegistry.setContextHoldingThread(this.glfwId, Thread.currentThread());
		GlStates.current().enable(GLES32.GL_DEBUG_OUTPUT);
		GLUtil.setupDebugMessageCallback(GLFWContext.logger.createPrintStream(GLFWContext.level));
	}

}
