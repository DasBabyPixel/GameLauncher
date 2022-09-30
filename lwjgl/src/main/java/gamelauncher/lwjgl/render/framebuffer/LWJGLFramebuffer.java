package gamelauncher.lwjgl.render.framebuffer;

import static org.lwjgl.opengles.GLES20.*;

import gamelauncher.engine.render.Window;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.render.states.GlStates;

@SuppressWarnings("javadoc")
public class LWJGLFramebuffer extends AbstractFramebuffer {

	private final int id;

	public LWJGLFramebuffer(Window window) {
		this(GlStates.current().genFramebuffers(), window);
	}

	public LWJGLFramebuffer(int id, Window window) {
		super(window);
		this.id = id;
	}

	public void bind() {
		GlStates.current().bindFramebuffer(GL_FRAMEBUFFER, id);
	}

	public void unbind() {
		GlStates.current().bindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public boolean isComplete() {
		try {
			bind();
			int status = GlStates.current().checkFramebufferStatus(GL_FRAMEBUFFER);
			return status == GL_FRAMEBUFFER_COMPLETE;
		} finally {
			unbind();
		}
	}

	public void checkComplete() throws GameException {
		if (!isComplete()) {
			try {
				bind();
				throw new GameException("Framebuffer not complete: Error "
						+ Integer.toHexString(GlStates.current().checkFramebufferStatus(GL_FRAMEBUFFER)));
			} finally {
				unbind();
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
		GlStates.current().deleteFramebuffers(id);
	}

	public int getId() {
		return id;
	}

}
