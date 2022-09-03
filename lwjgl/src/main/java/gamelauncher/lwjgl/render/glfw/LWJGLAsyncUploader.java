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
public class LWJGLAsyncUploader extends AbstractExecutorThread {

	final LWJGLGameLauncher launcher;

	final GLFWSecondaryContext secondaryContext;

	final Logger logger;

	public LWJGLAsyncUploader(LWJGLGameLauncher launcher) {
		super(launcher.getGlThreadGroup());
		this.launcher = launcher;
		this.secondaryContext = launcher.getWindow().getSecondaryContext();
		this.logger = Logger.getLogger();
		setName("GL-AsyncUploader");
	}

	@Override
	protected void startExecuting() {
		this.secondaryContext.makeCurrent();
		logger.debugf("GL-AsyncUploader: ThreadName: %s, Priority: %s", this.getName(), this.getPriority());
		GlStates.current().enable(GL_DEBUG_OUTPUT);
		GLUtil.setupDebugMessageCallback();

	}

	@Override
	protected void stopExecuting() {
		launcher.getGlThreadGroup().terminated(this);
		try {
			StateRegistry.removeContext(this.secondaryContext.getGLFWId());
		} catch (GameException ex) {
			ex.printStackTrace();
		}
		this.secondaryContext.destroyCurrent();
		try {
			Threads.waitFor(launcher.getGLFWThread().submit(() -> {
				secondaryContext.cleanup();
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
