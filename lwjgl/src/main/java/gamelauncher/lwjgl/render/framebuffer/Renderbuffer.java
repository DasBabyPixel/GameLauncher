package gamelauncher.lwjgl.render.framebuffer;

import static org.lwjgl.opengles.GLES20.*;

import gamelauncher.engine.util.function.GameResource;
import gamelauncher.lwjgl.render.states.GlStates;

@SuppressWarnings("javadoc")
public class Renderbuffer implements GameResource {

	private final int id;
	private int width, height;
	private int format;

	public Renderbuffer(int format, int width, int height) {
		this.format = format;
		id = GlStates.current().genRenderbuffers();
		resize(width, height);
	}

	public void resize(int width, int height) {
		if (this.width == width && this.height == height) {
			return;
		}
		this.width = width;
		this.height = height;
		bind();
		GlStates.current().renderbufferStorage(GL_RENDERBUFFER, format, width, height);
		unbind();
	}

	@Override
	public void cleanup() {
		GlStates.current().deleteRenderbuffers(id);
	}

	public int getId() {
		return id;
	}

	public void bind() {
		GlStates.current().bindRenderbuffer(GL_RENDERBUFFER, id);
	}

	public void unbind() {
		GlStates.current().bindRenderbuffer(GL_RENDERBUFFER, 0);
	}
}
