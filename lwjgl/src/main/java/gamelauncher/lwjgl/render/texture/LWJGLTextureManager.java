package gamelauncher.lwjgl.render.texture;

import java.util.concurrent.CompletableFuture;

import gamelauncher.engine.render.texture.TextureManager;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.ExecutorThreadService;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.glfw.GLFWFrame;
import gamelauncher.lwjgl.render.states.ContextLocal;

@SuppressWarnings("javadoc")
public class LWJGLTextureManager extends AbstractGameResource implements TextureManager {

	public final ExecutorThreadService service;

	public final LWJGLGameLauncher launcher;

	public final ContextLocal<CLTextureUtility> clTextureUtility;

	private GLFWFrame frame;

	public LWJGLTextureManager(LWJGLGameLauncher launcher) {
		this.launcher = launcher;
		this.clTextureUtility = CLTextureUtility.local(launcher);
		this.service = launcher.getThreads().cached;
		this.frame = null;
	}

	@Override
	public CompletableFuture<LWJGLTexture> createTexture() throws GameException {
		synchronized (this.frame) {
			if (this.frame == null) {
				this.frame = this.launcher.getMainFrame().newFrame();
			}
			return this.createTexture(this.frame.renderThread());
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
