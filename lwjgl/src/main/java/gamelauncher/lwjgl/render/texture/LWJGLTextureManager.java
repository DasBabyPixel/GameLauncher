package gamelauncher.lwjgl.render.texture;

import java.util.concurrent.CompletableFuture;

import gamelauncher.engine.render.texture.TextureManager;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.ExecutorThreadService;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.states.ContextLocal;

@SuppressWarnings("javadoc")
public class LWJGLTextureManager implements TextureManager {

	public final ExecutorThreadService service;

	public final LWJGLGameLauncher launcher;
	
	public final ContextLocal<CLTextureUtility> clTextureUtility;

	public LWJGLTextureManager(LWJGLGameLauncher launcher) {
		this.launcher = launcher;
		this.clTextureUtility = CLTextureUtility.local(launcher);
		this.service = launcher.getThreads().cached;
	}

	@Override
	public CompletableFuture<LWJGLTexture> createTexture() {
		return createTexture(launcher.getAsyncUploader());
//		return createTexture(launcher.getWindow().getRenderThread());
	}

	public CompletableFuture<LWJGLTexture> createTexture(ExecutorThread owner) {
//		return LWJGLTexture.newTexture(this, owner);
		return CompletableFuture.completedFuture(new LWJGLTexture(launcher, owner, service));
	}

	@Override
	public void cleanup() throws GameException {
//		launcher.getThreads().shutdown(service);
	}

}
