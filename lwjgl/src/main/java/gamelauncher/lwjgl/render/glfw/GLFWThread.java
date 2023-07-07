package gamelauncher.lwjgl.render.glfw;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.concurrent.AbstractExecutorThread;
import gamelauncher.engine.util.logging.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.APIUtil;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class GLFWThread extends AbstractExecutorThread {

    private final CompletableFuture<Void> terminateFuture = new CompletableFuture<>();
    private final Collection<GLFWUser> users = ConcurrentHashMap.newKeySet();
    private final Logger logger = Logger.logger();
    private final GLFWMonitorManager monitorManager;
    private final GLFWErrorCallback callback = new GLFWErrorCallback() {
        final Map<Integer, String> ERROR_CODES = APIUtil.apiClassTokens((field, value) -> 0x10000 < value && value < 0x20000, null, GLFW.class);

        @Override public void invoke(int errorcode, long descriptionp) {
            String description = GLFWErrorCallback.getDescription(descriptionp);
            String error = ERROR_CODES.get(errorcode);
            Thread.dumpStack();
            logger.errorf("GLFW Error: %s(%s)\nDescription: %s", error, Integer.toHexString(errorcode), description);
        }
    };
    private boolean initialized = false;

    public GLFWThread(GameLauncher launcher) {
        super(launcher, null);
        this.monitorManager = new GLFWMonitorManager(launcher);
        this.setName("GLFW-Thread");
    }

    public GLFWMonitorManager getMonitorManager() {
        return this.monitorManager;
    }

    @Override protected void startExecuting() {
        GLFW.glfwSetErrorCallback(callback);
        if (!GLFW.glfwInit()) {
            throw new ExceptionInInitializerError("Couldn't initialize GLFW");
        }
        initialized = true;

        this.monitorManager.init();
    }

    @Override protected void stopExecuting() {
        for (GLFWUser user : this.users) {
            user.destroy();
        }
        do {
            this.waitForSignal();
            this.workQueue();
        } while (!this.users.isEmpty());
        callback.free();
        this.monitorManager.cleanup();
        GLFW.glfwTerminate();
        this.terminateFuture.complete(null);
    }

    //	@Override
    //	protected boolean useCondition() {
    //		return false;
    //	}

    @Override protected void workExecution() {
        GLFW.glfwWaitEventsTimeout(0.5D);
        GLFW.glfwPollEvents();
    }

    @Override protected boolean shouldWaitForSignal() {
        return false;
    }

    @Override protected void signal() {
        if (!this.exit && initialized) {
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
