package gamelauncher.lwjgl.render.texture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import gamelauncher.engine.render.texture.TextureManager;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.lwjgl.LWJGLGameLauncher;

@SuppressWarnings("javadoc")
public class LWJGLTextureManager implements TextureManager {

	public final ExecutorService service;
	public final LWJGLGameLauncher launcher;

	public LWJGLTextureManager(LWJGLGameLauncher launcher) {
		this.launcher = launcher;
		this.service = launcher.getThreads().cached;
	}

	@Override
	public CompletableFuture<LWJGLTexture> createTexture() {
		return createTexture(launcher.getAsyncUploader());
//		return createTexture(launcher.getWindow().getRenderThread());
	}
	
	public CompletableFuture<LWJGLTexture> createTexture(ExecutorThread owner) {
		return LWJGLTexture.newTexture(this, owner);
	}

	@Override
	public void cleanup() throws GameException {
//		launcher.getThreads().shutdown(service);
	}
}
