package gamelauncher.gles.framebuffer;

import gamelauncher.engine.render.Frame;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.states.StateRegistry;
import java8.util.concurrent.CompletableFuture;

import static gamelauncher.gles.gl.GLES20.GL_FRAMEBUFFER;
import static gamelauncher.gles.gl.GLES20.GL_FRAMEBUFFER_COMPLETE;

public class GLESFramebuffer extends AbstractFramebuffer {

    private final int id;

    public GLESFramebuffer(Frame frame) {
        this(StateRegistry.currentGl().glGenFramebuffers(), frame);
    }

    public GLESFramebuffer(int id, Frame frame) {
        super(frame);
        this.id = id;
    }

    public void bind() {
        StateRegistry.currentGl().glBindFramebuffer(GL_FRAMEBUFFER, this.id);
    }

    public void unbind() {
        StateRegistry.currentGl().glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /**
     * @return if the framebuffer is complete. Must be bound before with {@link #bind()}
     */
    protected boolean isComplete() {
        return StateRegistry.currentGl().glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE;
    }

    /**
     * Checks if the framebuffer is complete. Must be bound before with {@link #bind()}
     *
     * @throws GameException
     */
    protected void checkComplete() throws GameException {
        if (!this.isComplete()) {
            throw new GameException("Framebuffer not complete: Error " + Integer.toHexString(StateRegistry.currentGl().glCheckFramebufferStatus(GL_FRAMEBUFFER)));
        }
    }

    @Override public void beginFrame() {
    }

    @Override public void endFrame() {
    }

    @Override protected CompletableFuture<Void> cleanup0() throws GameException {
        StateRegistry.currentGl().glDeleteFramebuffers(1, new int[]{this.id}, 0);
        return null;
    }

    public int getId() {
        return this.id;
    }

}
