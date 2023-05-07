package gamelauncher.gles;

import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.ScissorStack;
import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.states.StateRegistry;

/**
 * @author DasBabyPixel
 */
public class GLESScissorStack extends ScissorStack {

    private final Framebuffer framebuffer;

    public GLESScissorStack(Framebuffer framebuffer) {
        this.framebuffer = framebuffer;
    }

    @Override public void enableScissor() {
        StateRegistry.currentGl().glEnable(GLES20.GL_SCISSOR_TEST);
    }

    @Override public void setScissor(Scissor scissor) {
        StateRegistry.currentGl().glScissor(scissor.x(), framebuffer.height().intValue() - scissor.y() - scissor.h(), scissor.w(), scissor.h());
    }

    @Override public void disableScissor() {
        StateRegistry.currentGl().glDisable(GLES20.GL_SCISSOR_TEST);
    }

}
