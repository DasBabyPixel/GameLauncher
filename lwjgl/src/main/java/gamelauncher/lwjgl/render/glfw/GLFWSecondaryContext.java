package gamelauncher.lwjgl.render.glfw;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengles.GLES20.*;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

import org.lwjgl.opengles.GLES;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameResource;
import gamelauncher.lwjgl.render.states.StateRegistry;

@SuppressWarnings("javadoc")
public class GLFWSecondaryContext implements GameResource {

	private final GLFWWindow window;
	private final long id;
	private final AtomicReference<Thread> current = new AtomicReference<>();

	public GLFWSecondaryContext(GLFWWindow window) {
		this.window = window;
		window.renderThread.bindContext();
		int id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 1, 1, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
		glBindTexture(GL_TEXTURE_2D, 0);
		System.out.println(glIsTexture(id));
//		window.renderThread.releaseContext();
		glfwMakeContextCurrent(0);
		GLES.setCapabilities(null);

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_ES_API);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		this.id = glfwCreateWindow(1, 1, "unused", 0, window.getId());
		System.out.println(this.id);
		window.renderThread.releaseContext();
		makeCurrent();
		System.out.println(glIsTexture(id));
	}

	public boolean isCurrent() {
		return Thread.currentThread() == current.get();
	}

	public void makeCurrent() {
		System.out.println("Current in " + Thread.currentThread());
		current.set(Thread.currentThread());
		glfwMakeContextCurrent(id);
		GLES.createCapabilities();
		StateRegistry.setContextHoldingThread(id, Thread.currentThread());
	}

	public void destroyCurrent() {
		System.out.println("Destroy in " + Thread.currentThread());
		current.set(null);
		glfwMakeContextCurrent(0);
		GLES.setCapabilities(null);
		StateRegistry.setContextHoldingThread(id, null);
	}

	@Override
	public void cleanup() throws GameException {
		Threads.waitFor(window.glfwThread.submit(() -> {
			glfwDestroyWindow(id);
		}));
	}
}
