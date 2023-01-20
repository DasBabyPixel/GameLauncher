package gamelauncher.lwjgl.render.framebuffer;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.states.GlStates;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;
import org.lwjgl.opengles.GLES20;
import org.lwjgl.opengles.GLES30;

import java.util.concurrent.CompletableFuture;

public class BasicFramebuffer extends LWJGLFramebuffer {

	//	private int width, height;
	private final LWJGLTexture colorTexture;

	private final Renderbuffer depthStencilRenderbuffer;

	public BasicFramebuffer(LWJGLGameLauncher launcher, int width, int height)
			throws GameException {
		super(launcher.mainFrame());
		this.width().setNumber(width);
		this.height().setNumber(height);
		this.bind();

		GlStates c = GlStates.current();
		this.colorTexture = launcher.textureManager()
				.createTexture((ExecutorThread) Threads.currentThread());
		Threads.waitFor(
				this.colorTexture.allocate(this.width().intValue(), this.height().intValue()));
		c.framebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
				GLES20.GL_TEXTURE_2D, this.colorTexture.getTextureId(), 0);
		this.depthStencilRenderbuffer =
				new Renderbuffer(GLES30.GL_DEPTH24_STENCIL8, this.width().intValue(),
						this.height().intValue());
		c.framebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES30.GL_DEPTH_STENCIL_ATTACHMENT,
				GLES20.GL_RENDERBUFFER, this.depthStencilRenderbuffer.getId());
		this.checkComplete();
		this.unbind();
	}

	public void resize(int width, int height) throws GameException {
		if (this.width().intValue() == width && this.height().intValue() == height) {
			return;
		}
		this.width().setNumber(width);
		this.height().setNumber(height);
		Threads.waitFor(this.resizeColorTexture());
		this.bind();
		GlStates.current().framebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
				GLES20.GL_TEXTURE_2D, this.colorTexture.getTextureId(), 0);
		this.unbind();
		this.checkComplete();
		this.resizeDepthStencilRenderbuffer();
	}

	public LWJGLTexture getColorTexture() {
		return this.colorTexture;
	}

	public Renderbuffer getDepthStencilRenderbuffer() {
		return this.depthStencilRenderbuffer;
	}

	private void resizeDepthStencilRenderbuffer() {
		this.depthStencilRenderbuffer.resize(this.width().intValue(), this.height().intValue());
	}

	private CompletableFuture<Void> resizeColorTexture() {
		return this.colorTexture.allocate(this.width().intValue(), this.height().intValue());
	}

	@Override
	public void cleanup0() throws GameException {
		this.colorTexture.cleanup();
		this.depthStencilRenderbuffer.cleanup();
		super.cleanup0();
	}

}
