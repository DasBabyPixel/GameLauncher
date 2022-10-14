package gamelauncher.lwjgl.render.glfw;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.AbstractExecutorThread;

@SuppressWarnings("javadoc")
public class GLFWAsyncGL extends AbstractExecutorThread {

	private final GLFWFrame frame;

	public GLFWAsyncGL(GLFWFrame frame) {
		super(frame.launcher.getGlThreadGroup());
		this.frame = frame;
	}

	@Override
	protected void startExecuting() throws GameException {
	}

	@Override
	protected void stopExecuting() throws GameException {
	}

	@Override
	protected void workExecution() throws GameException {
	}

}
