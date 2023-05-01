package gamelauncher.gles.framebuffer;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.gles.states.StateRegistry;

import static gamelauncher.gles.gl.GLES20.GL_RENDERBUFFER;

public class Renderbuffer extends AbstractGameResource {

    private final int id;
    private final int format;
    private int width, height;

    public Renderbuffer(int format, int width, int height) {
        this.format = format;
        id = StateRegistry.currentGl().glGenRenderbuffers();
        resize(width, height);
    }

    public void resize(int width, int height) {
        if (this.width == width && this.height == height) {
            return;
        }
        this.width = width;
        this.height = height;
        bind();
        StateRegistry.currentGl().glRenderbufferStorage(GL_RENDERBUFFER, format, width, height);
        unbind();
    }

    @Override public void cleanup0() {
        StateRegistry.currentGl().glDeleteRenderbuffers(1, new int[]{id}, 0);
    }

    public int getId() {
        return id;
    }

    public void bind() {
        StateRegistry.currentGl().glBindRenderbuffer(GL_RENDERBUFFER, id);
    }

    public void unbind() {
        StateRegistry.currentGl().glBindRenderbuffer(GL_RENDERBUFFER, 0);
    }
}
