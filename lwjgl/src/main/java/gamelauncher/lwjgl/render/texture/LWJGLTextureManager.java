package gamelauncher.lwjgl.render.texture;

import java.util.concurrent.ExecutorService;

import gamelauncher.engine.render.texture.TextureManager;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.LWJGLGameLauncher;

@SuppressWarnings("javadoc")
public class LWJGLTextureManager implements TextureManager {

	public final ExecutorService service;
	public final LWJGLGameLauncher launcher;

	public LWJGLTextureManager(LWJGLGameLauncher launcher) {
		this.launcher = launcher;
		this.service = launcher.getThreads().newWorkStealingPool();
	}

	@Override
	public LWJGLTexture createTexture() {
		return new LWJGLTexture(this);
	}

	@Override
	public void cleanup() throws GameException {
		launcher.getThreads().shutdown(service);
	}
}
