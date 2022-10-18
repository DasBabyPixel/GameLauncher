package gamelauncher.lwjgl.render.glfw;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.glfw.GLFW;

import gamelauncher.engine.util.concurrent.AbstractExecutorThread;
import gamelauncher.engine.util.logging.Logger;

@SuppressWarnings("javadoc")
public class GLFWThread extends AbstractExecutorThread {

	private final CompletableFuture<Void> terminateFuture = new CompletableFuture<>();

	private final Collection<GLFWUser> users = ConcurrentHashMap.newKeySet();

	private final Logger logger = Logger.getLogger();

	private final GLFWMonitorManager monitorManager = new GLFWMonitorManager();

	public GLFWThread() {
		super(null);
		this.setName("GLFW-Thread");
	}

	@Override
	protected void startExecuting() {
		if (!GLFW.glfwInit()) {
			throw new ExceptionInInitializerError("Couldn't initialize GLFW");
		}
		this.monitorManager.init();
	}

	@Override
	protected void stopExecuting() {
		for (GLFWUser user : this.users) {
			user.destroy();
		}
		while (true) {
			this.waitForSignal();
			this.workQueue();
			if (this.users.isEmpty()) {
				break;
			}
		}
		if (!this.users.isEmpty()) {
			this.logger.errorf("Not all users of the GLFWThread have been cleared: %n%s", this.users);
		}
		this.monitorManager.cleanup();
		GLFW.glfwTerminate();
		this.terminateFuture.complete(null);
	}

	@Override
	protected void workExecution() {
		GLFW.glfwWaitEventsTimeout(0.5D);
		GLFW.glfwPollEvents();
	}

//	@Override
//	protected boolean useCondition() {
//		return false;
//	}

	@Override
	protected boolean shouldWaitForSignal() {
		return false;
	}

	public GLFWMonitorManager getMonitorManager() {
		return this.monitorManager;
	}

	@Override
	protected void signal() {
		if (!this.exit) {
			GLFW.glfwPostEmptyEvent();
		}
		super.signal();
	}

	void addUser(GLFWUser user) {
		this.users.add(user);
		this.signal();
	}

	void removeUser(GLFWUser user) {
		this.users.remove(user);
		this.signal();
	}

}
