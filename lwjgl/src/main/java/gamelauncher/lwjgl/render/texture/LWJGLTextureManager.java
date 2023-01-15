package gamelauncher.lwjgl.render.texture;

import gamelauncher.engine.render.texture.TextureManager;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.ExecutorThreadService;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.glfw.GLFWFrame;
import gamelauncher.lwjgl.render.states.ContextLocal;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LWJGLTextureManager extends AbstractGameResource implements TextureManager {

	public final ExecutorThreadService service;

	public final LWJGLGameLauncher launcher;

	public final ContextLocal<CLTextureUtility> clTextureUtility;
	private final Lock lock = new ReentrantLock(true);
	private GLFWFrame frame;

	public LWJGLTextureManager(LWJGLGameLauncher launcher) {
		this.launcher = launcher;
		this.clTextureUtility = CLTextureUtility.local(launcher);
		this.service = launcher.threads().cached;
		this.frame = null;
	}

	@Override
	public LWJGLTexture createTexture() throws GameException {
		try {
			lock.lock();
			if (this.frame == null) {
				this.frame = this.launcher.mainFrame().newFrame();
			}
			return this.createTexture(this.frame.renderThread());
		} finally {
			lock.unlock();
		}
	}

	public LWJGLTexture createTexture(ExecutorThread owner) {
		return new LWJGLTexture(this.launcher, owner, this.service);
	}

	@Override
	public void cleanup0() throws GameException {
		if (frame != null)
			frame.cleanup();
	}
}
