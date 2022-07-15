package gamelauncher.lwjgl.render.framebuffer;

import static org.lwjgl.opengles.GLES20.*;
import static org.lwjgl.opengles.GLES30.*;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;

@SuppressWarnings("javadoc")
public class BasicFramebuffer extends LWJGLFramebuffer {

//	private int width, height;
	private LWJGLTexture colorTexture;
	private Renderbuffer depthStencilRenderbuffer;

	public BasicFramebuffer(LWJGLGameLauncher launcher, int width, int height) throws GameException {
		super();
		width().setNumber(width);
		height().setNumber(height);
		bind();
		colorTexture = Threads.waitFor(launcher.getTextureManager().createTexture((ExecutorThread) Thread.currentThread()));
		resizeColorTexture();
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTexture.getTextureId(), 0);
		depthStencilRenderbuffer = new Renderbuffer(GL_DEPTH24_STENCIL8, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER,
				depthStencilRenderbuffer.getId());
		checkComplete();
//		glColorMask(true, true, true, true);
//		glClearColor(0, 0, 0, 0);
		unbind();
	}

	public void resize(int width, int height) {
		if (width().intValue() == width && height().intValue() == height) {
			return;
		}
		width().setNumber(width);
		height().setNumber(height);
		resizeColorTexture();
		resizeDepthStencilRenderbuffer();
	}

	public LWJGLTexture getColorTexture() {
		return colorTexture;
	}

	public Renderbuffer getDepthStencilRenderbuffer() {
		return depthStencilRenderbuffer;
	}

	private void resizeDepthStencilRenderbuffer() {
		depthStencilRenderbuffer.resize(width().intValue(), height().intValue());
	}

	private void resizeColorTexture() {
		colorTexture.allocate(width().intValue(), height().intValue());
	}

	@Override
	public void cleanup() throws GameException {
		colorTexture.cleanup();
		depthStencilRenderbuffer.cleanup();
		super.cleanup();
	}
}
