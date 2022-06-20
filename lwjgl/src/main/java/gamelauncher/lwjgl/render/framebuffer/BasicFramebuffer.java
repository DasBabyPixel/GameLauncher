package gamelauncher.lwjgl.render.framebuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

import gamelauncher.engine.GameException;
import gamelauncher.lwjgl.render.LWJGLTexture;

public class BasicFramebuffer extends Framebuffer {

	private int width, height;
	private LWJGLTexture colorTexture;
	private Renderbuffer depthStencilRenderbuffer;

	public BasicFramebuffer(int width, int height) throws GameException {
		super();
		this.width = width;
		this.height = height;
		bind();
		colorTexture = new LWJGLTexture();
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
		if (this.width == width && this.height == height) {
			return;
		}
		this.width = width;
		this.height = height;
		resizeColorTexture();
		resizeDepthStencilRenderbuffer();
	}

	public LWJGLTexture getColorTexture() {
		return colorTexture;
	}

	public Renderbuffer getDepthStencilRenderbuffer() {
		return depthStencilRenderbuffer;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	private void resizeDepthStencilRenderbuffer() {
		depthStencilRenderbuffer.resize(width, height);
	}

	private void resizeColorTexture() {
		colorTexture.allocate(width, height);
//		GlStates.bindTexture(GL_TEXTURE_2D, colorTexture.getTextureId());
//		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//		GlStates.bindTexture(GL_TEXTURE_2D, 0);
	}

	@Override
	public void cleanup() throws GameException {
		colorTexture.cleanup();
		depthStencilRenderbuffer.cleanup();
		super.cleanup();
	}
}
