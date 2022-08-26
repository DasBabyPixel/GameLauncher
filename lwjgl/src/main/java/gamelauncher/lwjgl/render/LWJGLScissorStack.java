package gamelauncher.lwjgl.render;

import static org.lwjgl.opengles.GLES20.*;

import gamelauncher.engine.render.ScissorStack;
import gamelauncher.lwjgl.render.states.GlStates;

/**
 * @author DasBabyPixel
 */
public class LWJGLScissorStack extends ScissorStack {

	@Override
	protected void enableScissor() {
		GlStates.current().enable(GL_SCISSOR_TEST);
	}

	@Override
	protected void setScissor(Scissor scissor) {
		GlStates.current().scissor(scissor.x, scissor.y, scissor.w, scissor.h);
	}

	@Override
	protected void disableScissor() {
		GlStates.current().disable(GL_SCISSOR_TEST);
	}

}
