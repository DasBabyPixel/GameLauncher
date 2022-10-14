package gamelauncher.lwjgl.render.framebuffer;

import java.util.concurrent.CompletableFuture;

import org.lwjgl.opengles.GLES20;
import org.lwjgl.opengles.GLES30;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.states.GlStates;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;

@SuppressWarnings("javadoc")
public class BasicFramebuffer extends LWJGLFramebuffer {

//	private int width, height;
	private LWJGLTexture colorTexture;

	private Renderbuffer depthStencilRenderbuffer;

	public BasicFramebuffer(LWJGLGameLauncher launcher, int width, int height) throws GameException {
		super(launcher.getMainFrame());
		System.out.println(width + " | " + height);
		this.width().setNumber(width);
		this.height().setNumber(height);
		this.bind();

		GlStates c = GlStates.current();
		this.colorTexture = Threads
				.waitFor(launcher.getTextureManager().createTexture((ExecutorThread) Threads.currentThread()));
		Threads.waitFor(this.colorTexture.allocate(this.width().intValue(), this.height().intValue()));
		System.out.println(this.colorTexture.getTextureId());
		c.framebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D,
				this.colorTexture.getTextureId(), 0);
		this.depthStencilRenderbuffer = new Renderbuffer(GLES30.GL_DEPTH24_STENCIL8, this.width().intValue(),
				this.height().intValue());
		c.framebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES30.GL_DEPTH_STENCIL_ATTACHMENT, GLES20.GL_RENDERBUFFER,
				this.depthStencilRenderbuffer.getId());
		this.checkComplete();
//		glColorMask(true, true, true, true);
//		glClearColor(0, 0, 0, 0);
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
		GlStates.current()
				.framebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D,
						this.colorTexture.getTextureId(), 0);
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
