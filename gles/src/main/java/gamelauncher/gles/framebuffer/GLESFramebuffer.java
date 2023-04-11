package gamelauncher.gles.framebuffer;

import gamelauncher.engine.render.Frame;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.states.StateRegistry;

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

    public boolean isComplete() {
        try {
            this.bind();
            int status = StateRegistry.currentGl().glCheckFramebufferStatus(GL_FRAMEBUFFER);
            return status == GL_FRAMEBUFFER_COMPLETE;
        } finally {
            this.unbind();
        }
    }

    public void checkComplete() throws GameException {
        if (!this.isComplete()) {
            try {
                this.bind();
                throw new GameException("Framebuffer not complete: Error " + Integer.toHexString(
                        StateRegistry.currentGl().glCheckFramebufferStatus(GL_FRAMEBUFFER)));
            } finally {
                this.unbind();
            }
        }
    }

    @Override
    public void beginFrame() {
    }

    @Override
    public void endFrame() {
    }

    @Override
    protected void cleanup0() throws GameException {
        super.cleanup0();
        StateRegistry.currentGl().glDeleteFramebuffers(1, new int[]{this.id}, 0);
    }

    public int getId() {
        return this.id;
    }

}
