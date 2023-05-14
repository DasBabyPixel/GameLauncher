package gamelauncher.gles.framebuffer;

import gamelauncher.engine.util.GameException;
import gamelauncher.gles.GLES;
import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.render.GLESGameRenderer;
import gamelauncher.gles.states.StateRegistry;
import gamelauncher.gles.texture.GLESTexture;
import java8.util.concurrent.CompletableFuture;

import static gamelauncher.gles.gl.GLES20.*;
import static gamelauncher.gles.gl.GLES30.GL_DEPTH24_STENCIL8;
import static gamelauncher.gles.gl.GLES30.GL_DEPTH_STENCIL_ATTACHMENT;

/**
 * An extension of the {@link GLESFramebuffer} for the {@link GLESGameRenderer}
 */
public class BasicFramebuffer extends GLESFramebuffer {

    private final GLESTexture colorTexture;
    private final Renderbuffer depthStencilRenderbuffer;

    public BasicFramebuffer(GLES gles, int width, int height) throws GameException {
        super(gles.mainFrame());
        this.width().number(width);
        this.height().number(height);

        GLES20 c = StateRegistry.currentGl();
        this.colorTexture = gles.textureManager().createTexture(gles.launcher().executorThreadHelper().currentThread());
        this.colorTexture.allocate(this.width().intValue(), this.height().intValue()); // This will execute immediately, we do not have to wait for the future
        this.depthStencilRenderbuffer = new Renderbuffer(GL_DEPTH24_STENCIL8, this.width().intValue(), this.height().intValue());

        this.bind();
        c.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, colorTexture.getTextureId(), 0);
        c.glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, depthStencilRenderbuffer.getId());
        this.checkComplete();
        this.unbind();
    }

    public void resize(int width, int height) throws GameException {
        if (this.width().intValue() == width && this.height().intValue() == height) {
            return;
        }
        this.width().number(width);
        this.height().number(height);
        CompletableFuture<Void> fut = this.resizeColorTexture(); // This should also execute immediately, I still add a check just to be sure and throw an exception if an error occurs
        if (!fut.isDone()) throw new IllegalStateException("Wrong thread");
        this.bind();
        StateRegistry.currentGl().glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.colorTexture.getTextureId(), 0);
        this.unbind();
        this.checkComplete();
        this.resizeDepthStencilRenderbuffer();
    }

    public GLESTexture colorTexture() {
        return this.colorTexture;
    }

    public Renderbuffer depthStencilRenderbuffer() {
        return this.depthStencilRenderbuffer;
    }

    private void resizeDepthStencilRenderbuffer() {
        this.depthStencilRenderbuffer.resize(this.width().intValue(), this.height().intValue());
    }

    private CompletableFuture<Void> resizeColorTexture() {
        return this.colorTexture.allocate(this.width().intValue(), this.height().intValue());
    }

    @Override public CompletableFuture<Void> cleanup0() throws GameException {
        this.colorTexture.cleanup();
        this.depthStencilRenderbuffer.cleanup();
        super.cleanup0();
        return null;
    }
}
