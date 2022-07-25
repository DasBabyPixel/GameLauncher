package gamelauncher.lwjgl.render.framebuffer;

import static org.lwjgl.opengles.GLES20.*;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.render.states.GlStates;

@SuppressWarnings("javadoc")
public class LWJGLFramebuffer implements Framebuffer {

	private final int id;
	private final NumberValue width = NumberValue.zero();
	private final NumberValue height = NumberValue.zero();

	public LWJGLFramebuffer() {
		this(glGenFramebuffers());
	}

	public LWJGLFramebuffer(int id) {
		this.id = id;
	}

	@Override
	public NumberValue width() {
		return width;
	}

	@Override
	public NumberValue height() {
		return height;
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
			int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
			return status == GL_FRAMEBUFFER_COMPLETE;
		} finally {
			unbind();
		}
	}

	public void checkComplete() throws GameException {
		if (!isComplete()) {
			try {
				bind();
				throw new GameException("Framebuffer not complete: Error " + Integer.toHexString(glCheckFramebufferStatus(GL_FRAMEBUFFER)));
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
	public void cleanup() throws GameException {
		glDeleteFramebuffers(id);
	}

	public int getId() {
		return id;
	}
}
