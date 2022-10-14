package gamelauncher.lwjgl.render.glfw;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

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
		if (!glfwInit()) {
			throw new ExceptionInInitializerError("Couldn't initialize GLFW");
		}
		monitorManager.init();
	}

	@Override
	protected void stopExecuting() {
		for (GLFWUser user : users) {
			user.destroy();
		}
		while (true) {
			waitForSignal();
			workQueue();
			if (users.isEmpty()) {
				break;
			}
		}
		if (!users.isEmpty()) {
			logger.errorf("Not all users of the GLFWThread have been cleared: %n%s", users);
		}
		monitorManager.cleanup();
		glfwTerminate();
		this.terminateFuture.complete(null);
	}

	@Override
	protected void workExecution() {
		glfwWaitEventsTimeout(0.5D);
		glfwPollEvents();
	}

//	@Override
//	protected boolean useCondition() {
//		return false;
//	}

	@Override
	protected boolean shouldWaitForSignal() {
		return false;
	}

	@Override
	protected void signal() {
		if (!exit) {
			glfwPostEmptyEvent();
		}
		super.signal();
	}

	void addUser(GLFWUser user) {
		users.add(user);
		signal();
	}

	void removeUser(GLFWUser user) {
		users.remove(user);
		signal();
	}

}
