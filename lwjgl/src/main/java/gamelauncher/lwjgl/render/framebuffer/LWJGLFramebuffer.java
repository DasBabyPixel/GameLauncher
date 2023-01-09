package gamelauncher.lwjgl.render.framebuffer;

import gamelauncher.engine.render.Frame;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.render.states.GlStates;
import org.lwjgl.opengles.GLES20;

public class LWJGLFramebuffer extends AbstractFramebuffer {

	private final int id;

	public LWJGLFramebuffer(Frame frame) {
		this(GlStates.current().genFramebuffers(), frame);
	}

	public LWJGLFramebuffer(int id, Frame frame) {
		super(frame);
		this.id = id;
	}

	public void bind() {
		GlStates.current().bindFramebuffer(GLES20.GL_FRAMEBUFFER, this.id);
	}

	public void unbind() {
		GlStates.current().bindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	}

	public boolean isComplete() {
		try {
			this.bind();
			int status = GlStates.current().checkFramebufferStatus(GLES20.GL_FRAMEBUFFER);
			return status == GLES20.GL_FRAMEBUFFER_COMPLETE;
		} finally {
			this.unbind();
		}
	}

	public void checkComplete() throws GameException {
		if (!this.isComplete()) {
			try {
				this.bind();
				throw new GameException("Framebuffer not complete: Error " + Integer.toHexString(
						GlStates.current().checkFramebufferStatus(GLES20.GL_FRAMEBUFFER)));
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
		GlStates.current().deleteFramebuffers(this.id);
	}

	public int getId() {
		return this.id;
	}

}
