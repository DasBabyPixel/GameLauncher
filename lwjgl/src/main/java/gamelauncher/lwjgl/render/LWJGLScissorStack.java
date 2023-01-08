package gamelauncher.lwjgl.render;

import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.ScissorStack;
import gamelauncher.lwjgl.render.states.GlStates;

import static org.lwjgl.opengles.GLES20.GL_SCISSOR_TEST;

/**
 * @author DasBabyPixel
 */
public class LWJGLScissorStack extends ScissorStack {

	private final Framebuffer framebuffer;

	public LWJGLScissorStack(Framebuffer framebuffer) {
		this.framebuffer = framebuffer;
	}

	@Override
	public void enableScissor() {
		GlStates.current().enable(GL_SCISSOR_TEST);
	}

	@Override
	public void setScissor(Scissor scissor) {
		GlStates.current()
				.scissor(scissor.x(), framebuffer.height().intValue() - scissor.y() - scissor.h(),
						scissor.w(), scissor.h());
	}

	@Override
	public void disableScissor() {
		GlStates.current().disable(GL_SCISSOR_TEST);
	}

}
