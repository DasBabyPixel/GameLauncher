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
		GlStates.bindFramebuffer(GL_FRAMEBUFFER, id);
	}

	public void unbind() {
		GlStates.bindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public boolean isComplete() {
		bind();
		return glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE;
	}

	public void checkComplete() throws GameException {
		if (!isComplete()) {
			throw new GameException("Framebuffer not complete!");
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
