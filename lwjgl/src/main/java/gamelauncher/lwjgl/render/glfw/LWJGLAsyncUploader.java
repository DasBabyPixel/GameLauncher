package gamelauncher.lwjgl.render.glfw;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.AbstractExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.LWJGLGameLauncher;
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
		Threads.waitFor(launcher.getGLFWThread().submit(() -> {
			secondaryContext.cleanup();
		}));
	}

	@Override
	protected void workExecution() {
	}

	@Override
	public void cleanup0() throws GameException {
		Threads.waitFor(exit());
	}

}
