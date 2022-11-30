package gamelauncher.lwjgl.render.model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengles.GLES20;
import org.lwjgl.system.MemoryUtil;

import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.render.states.GlStates;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;

public class Texture2DModel extends AbstractGameResource implements Model {

	private final int vao;

	private final int posbuffer;

	private final int texbuffer;

	private final int idxbuffer;

	private final int vertexCount;

	private final LWJGLTexture texture;

	public Texture2DModel(LWJGLTexture texture) {
		this(texture, 0F, 1F, 1F, 0F);
	}

	public Texture2DModel(LWJGLTexture texture, float textureLeft, float textureTop, float textureRight,
			float textureBottom) {
		this.texture = texture;
		textureTop = 1 - textureTop;
		textureBottom = 1 - textureBottom;
		GlStates c = GlStates.current();
		//@formatter:off
		this.vao = c.genVertexArrays();
		c.bindVertexArray(this.vao);
		this.idxbuffer = c.genBuffers();
		IntBuffer ibuffer = MemoryUtil.memAllocInt(6);
		ibuffer.put(new int[] {
				0, 2, 1,
				0, 3, 2
		}).flip();
		this.vertexCount = 6;
		c.bindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, this.idxbuffer);
		c.bufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibuffer, GLES20.GL_STATIC_DRAW);
		MemoryUtil.memFree(ibuffer);
		this.posbuffer = c.genBuffers();
		FloatBuffer fbuffer = MemoryUtil.memAllocFloat(12);
		fbuffer.put(new float[] {
				-0.5F, -0.5F, 0F,
				-0.5F, 0.5F, 0F,
				0.5F, 0.5F, 0F,
				0.5F, -0.5F, 0F
		}).flip();
		c.bindBuffer(GLES20.GL_ARRAY_BUFFER, this.posbuffer);
		c.bufferData(GLES20.GL_ARRAY_BUFFER, fbuffer, GLES20.GL_STATIC_DRAW);
		MemoryUtil.memFree(fbuffer);
		c.vertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, 0);
		
		this.texbuffer = c.genBuffers();
		fbuffer = MemoryUtil.memAllocFloat(8);
		fbuffer.put(new float[] {
				textureLeft, textureBottom,
				textureLeft, textureTop,
				textureRight, textureTop,
				textureRight, textureBottom
		}).flip();
		c.bindBuffer(GLES20.GL_ARRAY_BUFFER, this.texbuffer);
		c.bufferData(GLES20.GL_ARRAY_BUFFER, fbuffer, GLES20.GL_STATIC_DRAW);
		MemoryUtil.memFree(fbuffer);
		c.vertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 0, 0);
		//@formatter:on
	}

	@Override
	public void render(ShaderProgram program) throws GameException {
		GlStates c = GlStates.current();
		program.uapplyLighting.set(0);
		program.uhasTexture.set(1);
		c.activeTexture(GLES20.GL_TEXTURE0);
		c.bindTexture(GLES20.GL_TEXTURE_2D, this.texture.getTextureId());
		c.bindVertexArray(this.vao);

		program.uploadUniforms();
		c.enableVertexAttribArray(0);
		c.enableVertexAttribArray(1);
		c.drawElements(GLES20.GL_TRIANGLES, this.vertexCount, GLES20.GL_UNSIGNED_INT, 0);
		c.disableVertexAttribArray(0);
		c.disableVertexAttribArray(1);
	}

	@Override
	public void cleanup0() throws GameException {
		GlStates c = GlStates.current();
		c.deleteBuffers(this.idxbuffer);
		c.deleteBuffers(this.posbuffer);
		c.deleteBuffers(this.texbuffer);
		c.deleteVertexArrays(this.vao);
	}

	public LWJGLTexture getTexture() {
		return this.texture;
	}

}
