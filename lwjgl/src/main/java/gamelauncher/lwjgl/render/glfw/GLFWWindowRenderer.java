package gamelauncher.lwjgl.render.glfw;

import static org.lwjgl.opengles.GLES32.*;

import java.util.concurrent.Phaser;
import java.util.concurrent.locks.ReentrantLock;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.AbstractExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.render.states.GlStates;

@SuppressWarnings("javadoc")
public class GLFWWindowRenderer extends AbstractExecutorThread {

	private final Logger logger = Logger.getLogger();

	private final GLFWWindowContext context;

	private final Phaser phaser = new Phaser(1);

	private final ReentrantLock lock = new ReentrantLock(true);

	private boolean hasContext = false;

	public GLFWWindowRenderer(ThreadGroup group, GLFWWindowContext context) {
		super(group);
		this.context = context;
		setName("GLFW-WindowRenderer");
	}

	@Override
	protected void startExecuting() {
		context.makeCurrent();
		logger.infof("WindowRenderer: ThreadName: %s, Priority: %s", this.getName(), this.getPriority());
		GlStates.current().enable(GL_DEBUG_OUTPUT);
		GLUtil.setupDebugMessageCallback();
	}

	@Override
	protected void stopExecuting() {
		context.destroyCurrent();
	}

	@Override
	protected void workExecution() {
	}

	public void grabContext() throws GameException {
		lock.lock();
		Threads.waitFor(submitFirst(() -> {
			destroyCurrent();
		}));
		context.makeCurrent();
	}

	public void releaseContext() throws GameException {
		context.destroyCurrent();
		Threads.waitFor(submitFirst(() -> {
			makeCurrent();
		}));
		lock.unlock();
	}

	private void makeCurrent() {
		hasContext = true;
		context.makeCurrent();
	}

	public void destroyCurrent() {
		context.destroyCurrent();
		hasContext = false;
	}

}
