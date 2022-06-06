package gamelauncher.lwjgl.render.framebuffer;

import static org.lwjgl.opengl.GL30.*;

import gamelauncher.engine.GameException;
import gamelauncher.engine.util.GameResource;
import gamelauncher.lwjgl.render.GlStates;

public class Framebuffer implements GameResource {

	private final int id;

	public Framebuffer() {
		this(glGenFramebuffers());
	}

	public Framebuffer(int id) {
		this.id = id;
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
	public void cleanup() throws GameException {
		glDeleteFramebuffers(id);
	}

	public int getId() {
		return id;
	}
}
