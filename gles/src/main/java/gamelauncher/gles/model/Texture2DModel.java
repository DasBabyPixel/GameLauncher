package gamelauncher.gles.model;

import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.gl.GLES30;
import gamelauncher.gles.states.StateRegistry;
import gamelauncher.gles.texture.GLESTexture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Texture2DModel extends AbstractGameResource implements Model {

    private final int vao;

    private final int posbuffer;

    private final int texbuffer;

    private final int idxbuffer;

    private final int vertexCount;

    private final GLESTexture texture;

    public Texture2DModel(GLESTexture texture) {
        this(texture, 0F, 1F, 1F, 0F);
    }

    public Texture2DModel(GLESTexture texture, float textureLeft, float textureTop, float textureRight,
                          float textureBottom) {
        this.texture = texture;
        textureTop = 1 - textureTop;
        textureBottom = 1 - textureBottom;
        GLES30 c = StateRegistry.currentGl();
        //@formatter:off
        this.vao = c.glGenVertexArrays();
        c.glBindVertexArray(this.vao);
        this.idxbuffer = c.glGenBuffers();
        IntBuffer ibuffer = texture.memoryManagement().allocDirectInt(6);
        ibuffer.put(new int[] {
                0, 2, 1,
                0, 3, 2
        }).flip();
        this.vertexCount = 6;
        c.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, this.idxbuffer);
        c.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibuffer, GLES20.GL_STATIC_DRAW);
        texture.memoryManagement().free(ibuffer);
        this.posbuffer = c.glGenBuffers();
        FloatBuffer fbuffer = texture.memoryManagement().allocDirectFloat(12);
        fbuffer.put(new float[] {
                -0.5F, -0.5F, 0F,
                -0.5F, 0.5F, 0F,
                0.5F, 0.5F, 0F,
                0.5F, -0.5F, 0F
        }).flip();
        c.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.posbuffer);
        c.glBufferData(GLES20.GL_ARRAY_BUFFER, fbuffer, GLES20.GL_STATIC_DRAW);
        texture.memoryManagement().free(fbuffer);
        c.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, 0);

        this.texbuffer = c.glGenBuffers();
        fbuffer = texture.memoryManagement().allocDirectFloat(8);
        fbuffer.put(new float[] {
                textureLeft, textureBottom,
                textureLeft, textureTop,
                textureRight, textureTop,
                textureRight, textureBottom
        }).flip();
        c.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.texbuffer);
        c.glBufferData(GLES20.GL_ARRAY_BUFFER, fbuffer, GLES20.GL_STATIC_DRAW);
        texture.memoryManagement().free(fbuffer);
        c.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 0, 0);
        //@formatter:on
    }

    @Override
    public void render(ShaderProgram program) throws GameException {
        GLES30 c = StateRegistry.currentGl();
        program.uapplyLighting.set(0);
        program.uhasTexture.set(1);
        c.glActiveTexture(GLES20.GL_TEXTURE0);
        c.glBindTexture(GLES20.GL_TEXTURE_2D, this.texture.getTextureId());
        c.glBindVertexArray(this.vao);

        program.uploadUniforms();
        c.glEnableVertexAttribArray(0);
        c.glEnableVertexAttribArray(1);
        c.glDrawElements(GLES20.GL_TRIANGLES, this.vertexCount, GLES20.GL_UNSIGNED_INT, 0);
        c.glDisableVertexAttribArray(0);
        c.glDisableVertexAttribArray(1);
    }

    @Override
    public void cleanup0() throws GameException {
        GLES30 c = StateRegistry.currentGl();
        c.glDeleteBuffers(3, new int[]{idxbuffer, posbuffer, texbuffer}, 0);
        c.glDeleteVertexArrays(1, new int[]{vao}, 0);
    }

    public GLESTexture getTexture() {
        return this.texture;
    }

}
