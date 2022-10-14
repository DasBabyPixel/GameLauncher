package gamelauncher.lwjgl.render.texture;

import java.util.concurrent.CompletableFuture;

import gamelauncher.engine.render.texture.TextureManager;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.ExecutorThreadService;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.glfw.old.AsyncOpenGL;
import gamelauncher.lwjgl.render.states.ContextLocal;

@SuppressWarnings("javadoc")
public class LWJGLTextureManager extends AbstractGameResource implements TextureManager {

	public final ExecutorThreadService service;

	public final LWJGLGameLauncher launcher;

	public final ContextLocal<CLTextureUtility> clTextureUtility;

	private AsyncOpenGL asyncOpenGL;

	public LWJGLTextureManager(LWJGLGameLauncher launcher) throws GameException {
		this.launcher = launcher;
		this.clTextureUtility = CLTextureUtility.local(launcher);
		this.service = launcher.getThreads().cached;
		this.asyncOpenGL = null;
	}

	@Override
	public CompletableFuture<LWJGLTexture> createTexture() throws GameException {
		synchronized (this.asyncOpenGL) {
			if (this.asyncOpenGL == null) {
				this.asyncOpenGL = new AsyncOpenGL(this.launcher.getMainFrame());
				this.asyncOpenGL.start();
			}
			return this.createTexture(this.asyncOpenGL);
		}
//		return createTexture(launcher.getWindow().getRenderThread());
	}

	public CompletableFuture<LWJGLTexture> createTexture(ExecutorThread owner) {
//		return LWJGLTexture.newTexture(this, owner);
		return CompletableFuture.completedFuture(new LWJGLTexture(this.launcher, owner, this.service));
	}

	@Override
	public void cleanup0() throws GameException {
//		launcher.getThreads().shutdown(service);
	}

}
