package gamelauncher.lwjgl.render.glfw;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.AbstractExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameResource;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.LWJGLGameLauncher;

@SuppressWarnings("javadoc")
public class LWJGLAsyncUploader extends AbstractExecutorThread implements GameResource {

	final LWJGLGameLauncher launcher;
	final GLFWSecondaryContext secondaryContext;
	final Logger logger;

	public LWJGLAsyncUploader(LWJGLGameLauncher launcher) {
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
		this.secondaryContext.destroyCurrent();
	}

	@Override
	protected void workExecution() {
	}

	@Override
	public void cleanup() throws GameException {
		Threads.waitFor(launcher.getGLFWThread().submit(() -> {
			secondaryContext.cleanup();
		}));
		Threads.waitFor(exit());
	}
}
