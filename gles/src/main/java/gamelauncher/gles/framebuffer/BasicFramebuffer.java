package gamelauncher.gles.framebuffer;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.gles.GLES;
import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.states.StateRegistry;
import gamelauncher.gles.texture.GLESTexture;

import java.util.concurrent.CompletableFuture;

import static gamelauncher.gles.gl.GLES20.*;
import static gamelauncher.gles.gl.GLES30.*;

public class BasicFramebuffer extends GLESFramebuffer {

    private final GLESTexture colorTexture;
    private final Renderbuffer depthStencilRenderbuffer;

    public BasicFramebuffer(GLES gles, int width, int height) throws GameException {
        super(gles.mainFrame());
        this.width().setNumber(width);
        this.height().setNumber(height);
        this.bind();

        GLES20 c = StateRegistry.currentGl();
        this.colorTexture = gles.textureManager().createTexture(gles.launcher().executorThreadHelper().currentThread());
        Threads.waitFor(this.colorTexture.allocate(this.width().intValue(), this.height().intValue()));
        c.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.colorTexture.getTextureId(), 0);
        this.depthStencilRenderbuffer = new Renderbuffer(GL_DEPTH24_STENCIL8, this.width().intValue(), this.height().intValue());
        c.glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, this.depthStencilRenderbuffer.getId());
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
        StateRegistry.currentGl().glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.colorTexture.getTextureId(), 0);
        this.unbind();
        this.checkComplete();
        this.resizeDepthStencilRenderbuffer();
    }

    public GLESTexture getColorTexture() {
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
