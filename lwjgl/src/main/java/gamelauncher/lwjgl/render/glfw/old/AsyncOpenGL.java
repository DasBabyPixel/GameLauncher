package gamelauncher.lwjgl.render.glfw.old;

import java.util.concurrent.atomic.AtomicInteger;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.AbstractExecutorThread;
import gamelauncher.engine.util.logging.Logger;

@SuppressWarnings("javadoc")
public class AsyncOpenGL extends AbstractExecutorThread {

	private static final Logger logger = Logger.getLogger();

	private static final AtomicInteger counter = new AtomicInteger();

	protected final int id;

	protected final GLFWFrame parent;

	protected final GLFWContext context;

	public AsyncOpenGL(GLFWFrame parent) throws GameException {
		super(parent.getLauncher().getGlThreadGroup());
		this.id = AsyncOpenGL.counter.incrementAndGet();
		this.parent = parent;
		this.context = parent.newContext(parent, this.hasWindow());
		this.setName("AsyncOpenGL-" + this.id);
	}
	
	protected boolean hasWindow() {
		return false;
	}

	@Override
	protected void startExecuting() throws GameException {
		AsyncOpenGL.logger.info("Starting " + this.getName());
		this.context.makeCurrent();
	}

	@Override
	protected void stopExecuting() throws GameException {
		this.parent.launcher.getGlThreadGroup().terminated(this);
		AsyncOpenGL.logger.info("Stopping " + this.getName());
		this.parent.freeContext(this.context);
	}

	@Override
	protected void workExecution() throws GameException {
	}

}
