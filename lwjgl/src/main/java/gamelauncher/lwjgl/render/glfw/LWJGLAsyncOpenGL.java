package gamelauncher.lwjgl.render.glfw;

import static org.lwjgl.opengles.GLES32.*;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.AbstractExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.states.GlStates;
import gamelauncher.lwjgl.render.states.StateRegistry;

@SuppressWarnings("javadoc")
public class LWJGLAsyncOpenGL extends AbstractExecutorThread {

	final LWJGLGameLauncher launcher;

	final GLFWGLContext context;

	final Logger logger;

	public LWJGLAsyncOpenGL(LWJGLGameLauncher launcher, GLFWWindow window) {
		super(launcher.getGlThreadGroup());
		this.launcher = launcher;
		this.context = window.createNewContext();
		this.logger = Logger.getLogger();
		setName("GL-AsyncOpenGL");
	}

	@Override
	protected void startExecuting() {
		this.context.makeCurrent();
		logger.debugf("GL-AsyncOpenGL: ThreadName: %s, Priority: %s", this.getName(), this.getPriority());
		GlStates.current().enable(GL_DEBUG_OUTPUT);
		GLUtil.setupDebugMessageCallback();

	}

	@Override
	protected void stopExecuting() {
		launcher.getGlThreadGroup().terminated(this);
		try {
			StateRegistry.removeContext(this.context.getGLFWId());
		} catch (GameException ex) {
			ex.printStackTrace();
		}
		this.context.destroyCurrent();
		try {
			Threads.waitFor(launcher.getGLFWThread().submit(() -> {
				context.cleanup();
			}));
		} catch (GameException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void workExecution() {
	}

	@Override
	public void cleanup0() throws GameException {
		Threads.waitFor(exit());
	}

}
