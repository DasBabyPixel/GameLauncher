package gamelauncher.lwjgl.render.model;

import static org.lwjgl.opengles.GLES20.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.render.states.GlStates;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;

@SuppressWarnings("javadoc")
public class Texture2DModel implements Model {

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
		vao = c.genVertexArrays();
		c.bindVertexArray(vao);
		idxbuffer = c.genBuffers();
		IntBuffer ibuffer = memAllocInt(6);
		ibuffer.put(new int[] {
				0, 2, 1,
				0, 3, 2
		}).flip();
		vertexCount = 6;
		c.bindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxbuffer);
		c.bufferData(GL_ELEMENT_ARRAY_BUFFER, ibuffer, GL_STATIC_DRAW);
		memFree(ibuffer);
		posbuffer = c.genBuffers();
		FloatBuffer fbuffer = memAllocFloat(12);
		fbuffer.put(new float[] {
				-0.5F, -0.5F, 0F,
				-0.5F, 0.5F, 0F,
				0.5F, 0.5F, 0F,
				0.5F, -0.5F, 0F
		}).flip();
		c.bindBuffer(GL_ARRAY_BUFFER, posbuffer);
		c.bufferData(GL_ARRAY_BUFFER, fbuffer, GL_STATIC_DRAW);
		memFree(fbuffer);
		c.vertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		
		texbuffer = c.genBuffers();
		fbuffer = memAllocFloat(8);
		fbuffer.put(new float[] {
				textureLeft, textureBottom,
				textureLeft, textureTop,
				textureRight, textureTop,
				textureRight, textureBottom
		}).flip();
		c.bindBuffer(GL_ARRAY_BUFFER, texbuffer);
		c.bufferData(GL_ARRAY_BUFFER, fbuffer, GL_STATIC_DRAW);
		memFree(fbuffer);
		c.vertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		//@formatter:on
	}

	@Override
	public void render(ShaderProgram program) throws GameException {
		GlStates c = GlStates.current();
		program.uapplyLighting.set(0).upload();
		c.activeTexture(GL_TEXTURE0);
		c.bindTexture(GL_TEXTURE_2D, texture.getTextureId());
		c.bindVertexArray(vao);

		program.uploadUniforms();
		c.enableVertexAttribArray(0);
		c.enableVertexAttribArray(1);
		c.drawElements(GL_TRIANGLES, this.vertexCount, GL_UNSIGNED_INT, 0);
		c.disableVertexAttribArray(0);
		c.disableVertexAttribArray(1);
	}

	@Override
	public void cleanup() throws GameException {
		GlStates c = GlStates.current();
		c.deleteBuffers(idxbuffer);
		c.deleteBuffers(posbuffer);
		c.deleteBuffers(texbuffer);
		c.deleteVertexArrays(vao);
	}
	
	
	public LWJGLTexture getTexture() {
		return texture;
	}

}
