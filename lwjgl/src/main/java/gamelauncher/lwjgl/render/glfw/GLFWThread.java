package gamelauncher.lwjgl.render.glfw;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.concurrent.AbstractExecutorThread;
import gamelauncher.engine.util.logging.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class GLFWThread extends AbstractExecutorThread {

    private final CompletableFuture<Void> terminateFuture = new CompletableFuture<>();

    private final Collection<GLFWUser> users = ConcurrentHashMap.newKeySet();

    private final Logger logger = Logger.logger();

    private final GLFWMonitorManager monitorManager = new GLFWMonitorManager();

    public GLFWThread(GameLauncher launcher) {
        super(launcher, null);
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
        do {
            this.waitForSignal();
            this.workQueue();
        } while (!this.users.isEmpty());
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

    @Override
    protected void signal() {
        if (!this.exit) {
            GLFW.glfwPostEmptyEvent();
        }
        super.signal();
    }

    public GLFWMonitorManager getMonitorManager() {
        return this.monitorManager;
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
